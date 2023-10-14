# JDK动态代理流程分析

## Spring AOP 创建动态代理对象
当AOP的源码走到这个位置的时候，此处便会通过调用 Proxy 的 `newProxyInstance` 方法来创建 JDK 的动态代理。

```java
	@Override
	public Object getProxy(@Nullable ClassLoader classLoader) {
		if (logger.isTraceEnabled()) {
			logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
		}
		// 创建一个JDK动态代理的实例对象
		return Proxy.newProxyInstance(determineClassLoader(classLoader), this.proxiedInterfaces, this);
	}
```

## newProxyInstance 分析（初步校验）
在创建代理前，首先会先做一些基础的校验，如空参校验，安全检查。
```java
@CallerSensitive
public static Object newProxyInstance(ClassLoader loader,
                                      Class<?>[] interfaces,
                                      InvocationHandler h) throws IllegalArgumentException {
    // 检查 InvocationHandler 是否为空
    Objects.requireNonNull(h);
    // 将需要代理的接口复制一份出来
    final Class<?>[] intfs = interfaces.clone();
    // 安全检查
    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
        checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
    }

    // ........
}
```

## newProxyInstance 分析（代理对象生成）
1. 此处 `getProxyClass0` 是一段很重要的逻辑，它会去生成代理对象的字节码，并将代理对象加载到JVM内存中。

```java
@CallerSensitive
public static Object newProxyInstance(ClassLoader loader,
                                      Class<?>[] interfaces,
                                      InvocationHandler h) throws IllegalArgumentException {
    // ........

    /*
     * Look up or generate the designated proxy class.
     * 查找或生成一个代理对象。
     * 生成步骤：生成字节码文件，并返回一个class对象
     */
    Class<?> cl = getProxyClass0(loader, intfs);

    // ........
}
```

2. 进入 getProxyClass0 方法分析一下，这个逻辑首先也是做了一段校验，接着就从代理类缓存中试着去获取代理类，如果存在则直接返回，这一步就是为了提高获取代理类的效率，不要每次都去生成。如果不存在则生成代理类，并将其加入缓存中。

```java
private static Class<?> getProxyClass0(ClassLoader loader, Class<?>... interfaces) {

    // 检查传入的接口数量是否超出了Java编译器所允许的上限（65535个）。
    if (interfaces.length > 65535) {
        throw new IllegalArgumentException("interface limit exceeded");
    }
    // 尝试从代理类缓存（proxyClassCache）中获取代理类。如果代理类已经存在于缓存中，get方法会直接返回这个已经存在的代理类。
    return proxyClassCache.get(loader, interfaces);
}
```

3. 接着走进 `proxyClassCache.get(loader, interfaces)` 方法来分析一下。这一步主要是做了一些基础校验，然后需要从缓存 `map` 中拿到对应的 value，也就是 `valuesMap` 这个参数。如果不存在，我们需要给他做一下初始化，初始化成一个空的 ConcurrentHashMap。

```java
public V get(K key, P parameter) {
    // 空参数校验
    Objects.requireNonNull(parameter);
    // 清除过期节点
    expungeStaleEntries();
    // 获取缓存key
    Object cacheKey = CacheKey.valueOf(key, refQueue);

    // lazily install the 2nd level valuesMap for the particular cacheKey
    // 惰性的为 map 的第二层级的 valuesMap 初始化
    ConcurrentMap<Object, Supplier<V>> valuesMap = map.get(cacheKey);

    if (valuesMap == null) {
        // 如果第二层级的 valuesMap 不存在，则调用 putIfAbsent 赋值一个空的 ConcurrentHashMap
        ConcurrentMap<Object, Supplier<V>> oldValuesMap
             = map.putIfAbsent(cacheKey, valuesMap = new ConcurrentHashMap<>());

        // 如果 map 缓存中已经被其他的线程赋值了，那么就拿到这个值
        if (oldValuesMap != null) {
            valuesMap = oldValuesMap;
        }
    }

    // ........
}
```

3. 接着代码往下走，这一步便是从 map 的第二层级 `ConcurrentMap<Object, Supplier<V>>` 中获取到对应的 Supplier，如果 Supplier 不存在，则需要创建一个 Factory 赋值给 Supplier，如果存在则直接获取就完事了。

接着，就需要通过 supplier 的 `get()` 方法来将代理类对应的字节码创建出来，并将字节码加载到JVM内存中，这一个方法便是这里的核心逻辑。

```java
public V get(K key, P parameter) {
    // ........

    // 拿到第二层级的 subKey
    Object subKey = Objects.requireNonNull(subKeyFactory.apply(key, parameter));
    // 然后从 valuesMap 中拿到对应的 supplier
    Supplier<V> supplier = valuesMap.get(subKey);
    Factory factory = null;

    // 接着自旋
    while (true) {
        // 判断从缓存中拿到的 supplier 是否不为空
        if (supplier != null) {
            // 如果不为空，则通过 supplier 来获取 value，这个 value 便是代理类。
            // 这一步 get 便是需要将代理类对应的字节码创建出来，并将字节码加载到JVM内存中
            V value = supplier.get(); // 【核心代码】
            // 如果 value 存在，则直接返回
            if (value != null) {
                return value;
            }
        }

        // 如果工厂没有初始化，则初始化
        if (factory == null) {
            factory = new Factory(key, parameter, subKey, valuesMap);
        }

        // 如果缓存中的 supplier 为空，则进行初始化
        if (supplier == null) {
            // 通过 putIfAbsent 将上面创建的工厂添加进去，factory 就是 supplier
            supplier = valuesMap.putIfAbsent(subKey, factory);
            // 如果没有其他线程先放了一个 factory 进去，就把新创建出来的 factory 赋值给局部变量 supplier
            if (supplier == null) {
                supplier = factory;
            }
        } else {
            // 如果缓存中存在了 supplier，则做一个替换操作，把旧的替换掉
            if (valuesMap.replace(subKey, supplier, factory)) {
                // 替换成功，把新创建出来的 factory 赋值给局部变量 supplier
                supplier = factory;
            } else {
                // 替换失败，则把旧的 factory 拿到 赋值给局部变量 supplier
                supplier = valuesMap.get(subKey);
            }
        }
        // 此处则继续自旋，再次判断 supplier 是否不为空，此次赋值后不为空，则会通过 supplier.get() 来获取代理对象的class了
    }    

    // ....... 
}
```

4. 然后再来看一下这个 supplier 里的 `get()` 方法是怎么调用的。此处跳转到 Factory 里的 `get()` 方法。这段代码的逻辑，主要也就是创建 value，也就是创建代理对象，只不过这里还需要保证线程安全，比如说缓存中的 supplier 被修改了就不能再往下执行了。

```java
private final class Factory implements Supplier<V> {
    private final K key;
    private final P parameter;
    private final Object subKey;
    private final ConcurrentMap<Object, Supplier<V>> valuesMap;
    Factory(K key, P parameter, Object subKey, ConcurrentMap<Object, Supplier<V>> valuesMap) {
        this.key = key;
        this.parameter = parameter;
        this.subKey = subKey;
        this.valuesMap = valuesMap;
    }
    @Override
    public synchronized V get() { // serialize access

        // 尝试从 valuesMap 中获取与 subKey 关联的 Supplier<V> 对象
        Supplier<V> supplier = valuesMap.get(subKey);

        // 如果获取到的 supplier 不等于 this，说明在等待期间有其他线程更改了缓存。
        // 在这种情况下，它返回 null，表示需要重试整个获取过程。
        if (supplier != this) {
            return null;
        }

        // 如果 supplier 等于 this，说明在等待期间没有其他线程更改了缓存。它继续执行以下步骤。

        V value = null;
        try {
            // 使用 valueFactory 创建一个新的值 value，并确保 value 不为 null。
            value = Objects.requireNonNull(valueFactory.apply(key, parameter)); // 【核心逻辑】
        } finally {
            // 如果 value 为 null，则将当前的 supplier（即 this）从 valuesMap 中删除。
            if (value == null) { // remove us on failure
                valuesMap.remove(subKey, this);
            }
        }
        assert value != null;

        // 将 value 包装成一个 CacheValue 对象，这个对象持有 value 的弱引用
        CacheValue<V> cacheValue = new CacheValue<>(value);

        // 将 CacheValue 对象放入 reverseMap 中
        reverseMap.put(cacheValue, Boolean.TRUE);

        // 用新的 CacheValue 对象替换当前的 supplier（即 this）
        if (!valuesMap.replace(subKey, this, cacheValue)) {
            throw new AssertionError("Should not reach here");
        }
        return value;
    }
}
```

5. 再然后看下真正创建代理对象的地方 `valueFactory.apply(key, parameter)`，它是通过 valueFactory 来做的，这个 valueFactory 就是 `ProxyClassFactory`，代理类工厂，顾名思义，这个工厂就是用来生产代理类的。

首先，这一段逻辑为非核心逻辑，大致上就是验证接口数组的有效性，确保生成的代理类中实现的接口是有效的。
```java
private static final class ProxyClassFactory implements BiFunction<ClassLoader, Class<?>[], Class<?>> {
    // 代理类命名的前缀
    private static final String proxyClassNamePrefix = "$Proxy";
    // 代理类命名的计数器
    private static final AtomicLong nextUniqueNumber = new AtomicLong();
    @Override
    public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {
        // 创建一个 map 存储接口类和一个 boolean 值
        Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);

        // 遍历传递给 Proxy.newProxyInstance 方法的接口数组 interfaces
        for (Class<?> intf : interfaces) {

            // 验证类加载器是否能够通过接口的名称加载出相同的class对象
            Class<?> interfaceClass = null;
            try {
                interfaceClass = Class.forName(intf.getName(), false, loader);
            } catch (ClassNotFoundException e) {
            }

            // 如果加载出来的class对象和传入的接口不一致，说明接口类在当前的类加载器中不可见
            if (interfaceClass != intf) {
                throw new IllegalArgumentException(intf + " is not visible from class loader");
            }
            
            // 校验加载出来的class，必须是接口
            if (!interfaceClass.isInterface()) {
                throw new IllegalArgumentException(interfaceClass.getName() + " is not an interface");
            }

            // 验证传入的接口数组中是否有重复的接口
            // interfaceSet 在put的时候，如果存在相同的键，则put方法会返回之前的值，如果返回了值则说明有重复的接口
            if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                throw new IllegalArgumentException("repeated interface: " + interfaceClass.getName());
            }
        }

        // ......
    }
}

```

6. 接下来的逻辑，就是确定要生成的代理类的包名和类名。逻辑是：如果传入的接口中有非公共接口，那么生成的代理类将位于这些非公共接口所在的包中。如果所有的接口都是公共接口，那么代理类将位于默认包 com.sun.proxy 中。默认代理类不会生成到文件系统中，可以在项目启动时在 VM 参数中配置参数 `-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true` 则可将代理类的字节码生成到文件系统下。

```java
// 代理类的包名
String proxyPkg = null;
// 访问修饰符，表示生成的代理类是一个public修饰的final类
int accessFlags = Modifier.PUBLIC | Modifier.FINAL;

// 对传递给 Proxy.newProxyInstance 方法的接口数组 interfaces 进行遍历
for (Class<?> intf : interfaces) {
    // 获取该接口的修饰符
    int flags = intf.getModifiers();

    // 如果接口不是 public 的
    if (!Modifier.isPublic(flags)) {

        // 将 accessFlags 设置为 Modifier.FINAL，因为非 public 的接口，
        // 生成的代理类应该是 final 的，防止外部类继承非 public 接口。
        accessFlags = Modifier.FINAL;

        // 获取接口的名称
        String name = intf.getName();
        // 查找接口全名中的最后一个点
        int n = name.lastIndexOf('.');
        // 如果接口 intf 的全名中没有点，即没有包名部分，那么 pkg 被设置为空字符串
        String pkg = ((n == -1) ? "" : name.substring(0, n + 1));

        // 如果 proxyPkg 还是 null，说明没有确定代理类的包名，那么就将上一步获取的包名作为代理类的包名
        if (proxyPkg == null) {
            proxyPkg = pkg;
        } else if (!pkg.equals(proxyPkg)) {
            // 如果 proxyPkg 不为空，但是和非公共接口的包名不相同，则说明非公共接口是来自不同的包下
            throw new IllegalArgumentException(
                "non-public interfaces from different packages");
        }
    }
}

// 如果代理包名还是为空，则取默认包名：com.sun.proxy.
if (proxyPkg == null) {
    proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";
}

// 生成唯一自增的代理类的编号
long num = nextUniqueNumber.getAndIncrement();
// 生成代理类的全限定类名，如：com.sun.proxy.$Proxy32
String proxyName = proxyPkg + proxyClassNamePrefix + num;
```

7. 接下来就是最核心最核心的逻辑了，生成字节码，还有加载到内存，以便后续可以通过 Class 对象使用该代理类。

```java
// 生成代理类的字节码文件
byte[] proxyClassFile = ProxyGenerator.generateProxyClass(proxyName, interfaces, accessFlags);

try {
    // 将生成的代理类字节码加载到内存中
    return defineClass0(loader, proxyName, proxyClassFile, 0, proxyClassFile.length);
} catch (ClassFormatError e) {
    // 加载代理类的过程中发生了 ClassFormatError，即代理类的字节码格式错误，那么会捕获这个异常并抛出 IllegalArgumentException 异常
    throw new IllegalArgumentException(e.toString());
}
```

通过 JDK动态代理 生成代理类的过程便是如此了。当此处执行完成之后，`Class<?> cl = getProxyClass0(loader, intfs);` 这段逻辑就能把创建好的代理类返回了。


8. 接下来便是最后一步了，这一步会将代理类创建成一个代理对象。该代理对象实现了指定的接口，并且会将方法调用委托给指定的 InvocationHandler。

```java
/*
 * Invoke its constructor with the designated invocation handler.
 */
try {
    if (sm != null) {
        checkNewProxyPermission(Reflection.getCallerClass(), cl);
    }
    // 获取代理类 cl 的构造函数
    final Constructor<?> cons = cl.getConstructor(constructorParams);
    final InvocationHandler ih = h;
    // 检查代理类 cl 是否是公共类（即是否具有 Modifier.PUBLIC 修饰符）
    if (!Modifier.isPublic(cl.getModifiers())) {
        // 如果代理类不是公共的，则将构造函数设置为可访问，以确保可以调用非公共构造函数
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                cons.setAccessible(true);
                return null;
            }
        });
    }
    // 通过构造函数的 newInstance 方法创建代理对象，传递 InvocationHandler 对象 h 作为构造函数参数
    return cons.newInstance(new Object[]{h});
} catch (IllegalAccessException|InstantiationException e) {
    throw new InternalError(e.toString(), e);
} catch (InvocationTargetException e) {
    Throwable t = e.getCause();
    if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
    } else {
        throw new InternalError(t.toString(), t);
    }
} catch (NoSuchMethodException e) {
    throw new InternalError(e.toString(), e);
}
```


## proxy字节码文件分析

当代理对象注册完成之后，我们可以看到测试代码中，通过 `context.getBean("loginServiceImpl", LoginService.class)` 便可以获取到上面创建的代理对象。此处则可以看到 loginServiceImpl 这个对象是 $Proxy32 类型的，说明我们的代理对象是正确创建了。此时则可以通过上面我们配置的参数 `-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true` 生成的字节码文件来分析一下，这一块调用 login 方法是怎么把切面的调用也加进去的。

```java
public class AopTest {
	@Test
	public void test1() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("aopDemo.xml");
		LoginService loginServiceImpl = context.getBean("loginServiceImpl", LoginService.class);
		loginServiceImpl.login("whoiszxl", "123456");
		loginServiceImpl.logout("token");
		context.close();
	}
}
```


定位到 com.sun.proxy.$Proxy32 类，代码量比较多，就挑选核心代码展示。

可以看到这个类实现了四个接口，第一个接口便是我们的业务接口，其他三个是我们在创建动态代理的时候传入的三个。接着它实现了一个构造方法，这个构造方法中会传入一个 InvocationHandler 参数，这个参数便是之前 `Proxy.newInstance` 中，有个地方会获取代理类的构造方法（cl.getConstructor(constructorParams)），这个获取到的构造方法便是如下这个。其中 constructorParams 参数便是一个只有一个参数的数组，其中的参数为 InvocationHandler.class，表示这个构造方法里的参数类型为 InvocationHandler.class，再往下走，看到 `cons.newInstance(new Object[]{h})` 的时候，此处便是通过这个构造方法来构建代理对象，其中传入的 h 参数，便是一个 JdkDynamicAopProxy，它实现了 InvocationHandler 接口，所以可以传入此处。这个 JdkDynamicAopProxy 便是我们 AOP 的动态代理对象。
```java
public final class $Proxy32 extends Proxy implements LoginService, SpringProxy, Advised, DecoratingProxy {
    //......
    
    // 构造方法，会将 var1（JdkDynamicAopProxy）参数赋值给父类 Proxy 的 `protected InvocationHandler h;` 参数，方便后续调用
    public $Proxy32(InvocationHandler var1) throws  {
        super(var1);
    }
}
```

接下来再看到字节码文件中我们的业务方法，可以看到它是实现了 LoginService 接口，所以它会生成此接口下的方法，比如说 login 方法。其中最主要是调用了 $Proxy32 的父类 Proxy 中的 h 属性的 invoke() 方法，h 也就是 InvocationHandler，InvocationHandler 中又有 AOP 的业务实现，所以此处调用代理类的 login，便是调用了 InvocationHandler 的 invoke() 方法。

```java
public final class $Proxy32 extends Proxy implements LoginService, SpringProxy, Advised, DecoratingProxy {
    //......
    
    public final String login(String var1, String var2) throws  {
        try {
            // this: 当前 proxy 对象
            // m3: 通过反射获取到 LoginService 中的 login 方法，参考最末尾的赋值操作
            // example: m3 = Class.forName("com.whoiszxl.aopDemo.LoginService").getMethod("login", Class.forName("java.lang.String"), Class.forName("java.lang.String"));
            // new Object[]{var1, var2}: 调用方法的参数
            return (String)super.h.invoke(this, m3, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }
}
```

在上一步传入的 h 参数，也就是 InvocationHandler，其实是一个 JdkDynamicAopProxy，所以此处调用 invoke 方法便是调用 JdkDynamicAopProxy 的。这里我们进一步分析。




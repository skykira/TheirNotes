# Spring 

## 项目中声明的 Bean 是如何被注册的？

> 归根结底，通过项目中声明的配置类。
> 
> 从 Spring 的 main 方法开始，通过 @ComponentScan 遍历找到路径下所有的 class 文件，注册为 BeanDefinition。
> 
> 找到其中的 @Configuration 类，解析与其相关的类注册为 BeanDefinition。

主要逻辑依赖 BeanDefinitionRegistry 后置处理器 `ConfigurationClassPostProcessor`。

1. 从 beanDefinitionRegistry 中找出所有配置类，作为候选，等待处理。

    - @Configuration 修饰的类
    - @Component、@ComponentScan、@Import、@ImportResource 修饰的类或者有 @Bean 方法的类

    一波筛选后，此时的候选 BeanDefinition 其实只有 `application`。

2. 构造 BeanNameGenerator，为待会注册的 BeanDefinition 生成 beanName
3. 创建配置类 ConfigurationClassParser，用于待会解析扫描出的所有配置类，并添加到名为 configurationClass 的 map 中。
4. parser.parse(candidates); //开始解析
   1. 解析内部成员类，递归解析
   
   2. 解析 @PropertySource 注解指示的类
   3. 解析 @ComponentScan 注解路径下的类，生成 BeanDefinition，如果存在被 @ComponentScan 修饰的类，递归解析
   4. 解析 @Import 注解导入的类

        1. 导入类类型为 `ImportSelector.class` 时

            1. 类型为 DeferredImportSelector.class 时

                添加到 `deferredImportSelectors`，等待处理

            2. 其他，递归处理，直到将需要导入的普通配置类处理完

        2. 导入 `ImportBeanDefinitionRegistrar.class` 类型时
            导入 `importBeanDefinitionRegistrars`，等待处理
        3. 都不是，作为普通配置类被解析
   5. 解析 @ImportResource 注解指示的类，填入 `importedResources` 中
   6. 解析 @Bean 修饰的方法引入的 bean
   7. 解析接口中默认方法可能引入的 bean
   8. 解析父类，返回然后递归解析
   9. 若全部递归完成，返回null，跳出解析过程
   10. `parse()` 方法返回前，处理 `deferredImportSelectors` 中需要延迟注册的 BeanDefinition，因为这些 BeanDefinition 注册有 condition，所以需要在其余的加载完后，再轮到他们

5. this.reader.loadBeanDefinitions(configClasses);
   
   将 configClasses 中的类解析为 BeanDefinition，只有 @ComponentScan 扫描出来的类全部变为了 BeanDefinition，其余的暂时存在于 configClass 中。

    1. registerBeanDefinitionForImportedConfigurationClass(configClass);

        将 ImportSelector.class 导入的类解析

    2. loadBeanDefinitionsForBeanMethod(beanMethod);

        将 BeanMethod 导入的类解析

    3. loadBeanDefinitionsFromImportedResources(configClass.getImportedResources());

        将上面存入 `importedResources` 中的类解析

    4. loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());

        将上面存入 `importBeanDefinitionRegistrars` 中的类处理掉，调用 `ImportBeanDefinitionRegistrar` 的接口方法 registrar.registerBeanDefinitions(metadata, this.registry)

6. loadBeanDefinitions 步骤中如果有新的 beanDefinition 被发现，加入 candidates，递归重新解析
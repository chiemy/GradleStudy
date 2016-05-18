# 项目主要结构

```
Project
   ├── gradlew
   ├── gradlew.bat
   ├── gradle/wrapper/
   │    ├── gradle-wrapper.jar
   │    └── gradle-wrapper.properties
   ├── build.gradle
   ├── settings.gradle
   ├── dependency.gradle
   └── app
       ├── build.gradle
       ├── build
       ├── libs
       └── src
           └── main
               ├── java
               │   └── com.package.myapp
               └── res
                   ├── drawable
                   ├── layout
                   └── etc.
 ```
 
 <br>
 
# 目录分析

## Project/gradlew、gradlew.bat
gradlew 是一个 shell 脚本文件，在 Mac 上运行。

gradlew.bat，在 windows 系统上运行。

当运行这些脚本时，对应于 gradle-wrapper.properties 文件内的 gradle 版本将会自动下载。

## Project/gradle/wrapper/gradle-wrapper.properties

```
#Mon Dec 28 10:00:20 PST 2015
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-2.10-all.zip
```


这里声明了 gradle 的目录与下载路径以及当前项目使用的 gradle 版本，这些默认的路径我们一般不会更改的，这个文件里指明的 gradle 版本不对也是很多导包不成功的原因之一。


## Project/build.gradle

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
// 从文件里获取配置信息
apply from: "dependency.gradle"
buildscript {
    repositories {
        // 代表着你的依赖包的来源，jCenter是一个新的中央仓库，兼容maven中心仓库，而且性能更优
        jcenter()
    }
    dependencies {
        // 从 jCenter 中下载 gradle 插件
        classpath 'com.android.tools.build:gradle:2.1.0'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        // 注意: 不要讲你应用的依赖放在这里; 他们属于单独的module build.gradle文件
        // 你不应该在该方法体内定义子模块的依赖包，你仅仅需要定义默认的Android插件就可以了，因为该插件可以让你执行相关Android的tasks。
    }
}

// 以用来定义各个模块的默认属性，你可以不仅仅局限于默认的配置，未来你可以在方法体内自己创造tasks，这些tasks将会在所有模块中可见。
allprojects {
    repositories {
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```


## Project/settings.gradle

```
include ':app'
```

这里定义了项目包含的模块，因为该项目只有一个 `app` 模块，所以是上边的样子。如果有过个模块将以逗号分隔，如

```
include ':app', ‘:module1’, ':module2'
```

## Project/dependency.gradle

```
// 声明了一个字符串
def supportLibVersion = "23.4.0"

ext{
    libSupportAppcompat = "com.android.support:appcompat-v7:${supportLibVersion}"

    // 下面这种方式也可以, 类似键值对的形式
    // 使用这种方式引用, compile rootProject.ext.dependencies.appcompatV7
//    dependencies = [
//            appcompatV7 : "com.android.support:appcompat-v7:${supportLibVersion}",
//    ]
}
```

这个不是项目生产的，是自行添加的，名称可随意，也可以不要后缀，这个后缀只是方便说明一个 gradle 相关的文件。此文件需要被引入，否则内容是使用不了的。在 `Project/build.gradle` 文件中，我们通过 `apply from: "dependency.gradle"` 进行了引入，看上文。

## Project/app/build.gradle

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.3"

    defaultConfig {
        applicationId "com.chiemy.example.gradlestudy"
        minSdkVersion 15
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    // 配置文件在, 项目根目录下的 dependency.gradle 文件夹内, 在根目录下的 build.gradle 通过apply from:引入
    // 这种方式在有多个 module 时非常有用, 避免修改依赖时, 需要修改多个module的问题
    compile rootProject.ext.libSupportAppcompat;

    // compile rootProject.ext.dependencies.appcompatV7
}
```

处于某个模块下的 build.gradle 文件只对当前模块起作用。所以这个文件只对app这个模块起作用。

```
apply plugin: 'com.android.application'
```

这里引入了一个插件，插件用于扩展 gradle 脚本的能力，在一个项目中使用插件，这样该项目的构建脚本就可以使用该插件定义好的属性和它的 tasks。

此插件是 google 的 Android 开发团队编写的插件，能够提供所有关于 Android 应用和依赖库的构建，打包和测试。

> 注意：当你在开发一个依赖库，那么你应该用 'com.android.library'，并且你不能同时使用他们2个，这将导致构建失败，一个模块要么使用 Android application 或者 Android library 插件，而不是二者。

### android{}
引入 `com.android.application` 插件后，我们可以使用该插件定义好的属性和方法，`android{}`代表了一个类，内部包含了 Android 的方法和属性。

```
// 编译该 app 时候，你想使用到的 API 版本。
compileSdkVersion：23 
// 构建工具的版本号。
buildToolsVersion："23.0.3" 
```

构建工具包含了很多实用的命令行命令，例如 aapt,zipalign,dx 等。

#### defaultConfig{}

```
defaultConfig {
	// 做为应用的唯一的标识，而不是 AndroidManifest 文件中定义的package name
	applicationId "com.chiemy.example.gradlestudy"
	// 支持设备的最小 API 版本
	minSdkVersion 15
	// 目标设备的 API 版本
	targetSdkVersion 23
	// 版本号
	versionCode 1
	// 版本名
	versionName "1.0"
}
```

在 gradle 被用来作为Android构建工具之前，AndroidManifest.xml 文件中的 package name 在有两个作用：1、app 的唯一标识，2、R 资源文件的包名(有时也是源码的包名)

为了方便的构建不同版本的应用，Android 开发团队将 package name 的两大功能拆分开，AndroidManifest 文件中定义的 package name 依然被用来作为包名和 R 文件的包名。而 applicationid 将被用在设备和各大应用商店中作为唯一的标示。

`minSdkVersion`、`targetSdkVersion`、`versionCode`、`versionName` 将会覆盖 AndroidManifest 中的相应信息，所以没必要在AndroidManifest中定义这些属性了。

### dependencies{}

这个是 Gradle 的通过方法，不是 android 特有的，所以在了 android{} 外部。

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    // 配置文件在项目根目录下的 dependency.gradle 文件夹内, 在根目录下的 build.gradle 通过apply from:引入
    // 这种方式在有多个 module 时非常有用, 避免修改依赖时, 需要修改多个 module 的问题
    compile rootProject.ext.libSupportAppcompat;

    // compile rootProject.ext.dependencies.appcompatV7
}
```

在使用 gradle 的之前，我们必须手动管理依赖，首先下载 jar 文件，复制到项目中，然后引用他们。因为这些 jar 包通常没有版本号，我们还要记住他们的版本号，当需要更新的时候，我们再去下载，然后再替换，这一切都很麻烦。对了，我们还要把 jar 包连同项目一起发布到 git 上，要不你的同事还得再去手动下载。

使用gradle之后这一切都太爽了，只需要做个依赖声明，依赖包会在你执行build构建的时候自动从远程仓库下载。当然 Gradle 会为你在本地保留缓存，所以一个特定版本的依赖包只需要下载一次。

Gradle 支持三种不同的仓库，分别是：Maven 和 Ivy 以及文件夹。

#### 依赖声明

一个依赖需要定义三个元素：

- group 意味着创建该 library 的组织名，通常这会是包名
- name 是该 library 的唯一标示。
- version 是该 library 的版本号

其实，


```
compile 'com.android.support:appcompat-v7:23.4.0'
```

完整的表述是这样的:

```
compile group: 'com.android.support', name: 'appcompat-v7', version:'23.4.0'
```

#### 仓库

为了方便，Gradle 会默认预定义三个 maven 仓库：Jcenter 和mavenCentral 以及本地 maven 仓库。你可以同时声明它们：

```
repositories {
	mavenCentral()
	jCenter()
	mavenLocal()
}
```

Maven 和 Jcenter 仓库是很出名的两大仓库。我们没必要同时使用他们，在这里我建议你们使用 jcenter，jcenter 是 maven 中心库的一个分支，这样你可以任意去切换这两个仓库。当然 jcenter 也支持了 https，而 maven 仓库并没有。

#### 远程非中央仓库

有些组织，创建了一些有意思的插件或者 library,他们更愿意把这些放在自己的 maven 库，而不是 maven 中心库或 jcenter。那么当你需要是要这些仓库的时候，你只需要在 maven 方法中加入 url 地址就好：

```
repositories {
       maven {
           url "http://repo.acmecorp.com/maven2"
       }
}
```

#### 本地依赖

**jar**

有些情况下，有的第三方库没有提供中央仓库的版本，或者我们使用自己的 jar 包，不想发布到公有的中央仓库，我们可以添加本地依赖，首先将 jar 文件添加到 `libs` 文件夹下，然后声明如下依赖：

```
dependencies {
	compile files('libs/mine.jar')
}
```

如果jar包很多时，我们可以这样做：

```
dependencies {
	compile fileTree(dir: 'libs', include: ['*.jar'])
}
```

**native包（so包）**

用 c 或者 c++ 写的 library 会被叫做 so 包，Android 插件默认情况下支持 native 包，我们只需要将 `.so` 文件放到相应的文件夹下即可，默认情况下（也可以指定）此文件夹为 module 根目录下的 `jniLibs`，如果没有需要手动创建。

**aar**

如果我们使用 aar 作为依赖包，首先我们需要建个文件夹存放 aar 文件，比如叫 aars。至于此文件夹放到哪，跟此 arr 应用的范围有关，如果是多个 module 都有引用，那么就建到项目跟目录下。如果是特定的 module 要引用，就建到相应的 module 下。然后，在相应的 build.gradle 内声明文件夹作为依赖库：

```
repositories {
    flatDir {
        dirs 'aars' 
    }
}
```
同时，声明依赖：

```
dependencies {
	compile(name:'aarFileName', ext:'aar')
}
```

这个会告诉 Gradle，在 aars 文件夹下，添加一个叫做 aarFileName 的文件，且其后缀是 aar, 作为依赖。

#### 动态版本

有时我们可能想使用最新版本的依赖包，即远程仓库依赖包的版本更新后，我们自动下载最新的依赖包。这时，我们可以使用动态版本。

如果你始终想获取最新版本：

```
compile 'com.android.support:appcompat-v7:+'
```

如果我们想固定主版本，想得到最新的 minor (次要)版本：

```
compile 'com.android.support:appcompat-v7:23.+'
```

如果我们想得到最新的 patch (补丁)版本：


```
compile 'com.android.support:appcompat-v7:23.+'
```

> 关于上边这种版本命名方式，找到了一个相关的说明，[APR版本规则](http://apr.apache.org/versioning.html)：[APR(Apache Portable Runtime)](http://apr.apache.org/)使用三个整数来记录版本号：MAJOR.MINOR.PATCH。MAJOR 表示当前 APR 的主版本号，它的变化通常意味着 APR 的巨大的变化，比如体系结构的重新设计，API 的重新设计等等，而且这种变化通常会导致APR版本的向前不兼容。MINOR 称之为 APR 的次版本号，它通常只反映了一些较大的更改，比如 APR 的 API 的增加等等，但是这些更改并不影响与旧版本源代码和二进制代码之间的兼容性。PATCH 通常称之为补丁版本，通常情况下如果只是对 APR 函数的修改而不影响 API 接口的话都会导致 PATCH 的变化。

如果你不知道如何正确使用动态版本，请尽量不要使用，如果你允许gradle去挑选最新版本，可能导致挑选的依赖版本并不是稳定版，这可能会使构建产生很多问题，更糟糕的是你可能在你的服务器和私人pc上得到不同的依赖版本，这直接导致你的应用不同步。

> 文章链接：[如何使用动态版本](https://brock.io/post/repeatable_android_builds/)

Android Studio也是不建议使用动态版本的，如果使用了动态版本，会出现如下警告：

<image src="capture/capture03.webp" width=600>

#### 使用Android Studio工具添加依赖库

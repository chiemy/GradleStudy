

# 目录结构


Project

   ├── [gradlew](#gradlew)
   
   ├── [gradlew.bat](#gradlew)
   
   ├── gradle/wrapper/
   
   │    　　　　　├── gradle-wrapper.jar
   
   │    　　　　　└── [gradle-wrapper.properties](#wrapper_pro)
   
   ├── [build.gradle](#root_build_gradle)
   
   ├── [settings.gradle](#setting_gradle)
   
   ├── [dependency.gradle](#dependency)
      
   └── app
   
　　　　├── [build.gradle](#app_gradle)

　　　　├── build

　　　　├── libs

　　　　└── src

　　　　　　　└── main
           
　　　　　　　　　　├── java


　　　　　　　　　　└── res


 


## <a name='gradlew'>Project/gradlew、gradlew.bat</a>
gradlew 是一个 shell 脚本文件，在 Mac 上运行。

gradlew.bat，在 windows 系统上运行。

当运行这些脚本时，对应于 gradle-wrapper.properties 文件内的 gradle 版本将会自动下载。

## <a name='wrapper_pro'>Project/gradle/wrapper/gradle-wrapper.properties</a>

```
#Mon Dec 28 10:00:20 PST 2015
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-2.10-all.zip
```


这里声明了 gradle 的目录与下载路径以及当前项目使用的 gradle 版本，这些默认的路径我们一般不会更改的，这个文件里指明的 gradle 版本不对也是很多导包不成功的原因之一。


## <a name='root_build_gradle'>Project/build.gradle</a>

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


## <a name='setting_gradle'>Project/settings.gradle

```
include ':app'
```

这里定义了项目包含的模块，因为该项目只有一个 `app` 模块，所以是上边的样子。如果有过个模块将以逗号分隔，如

```
include ':app', ‘:module1’, ':module2'
```

## <a name='dependency'>Project/dependency.gradle</a>

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

## <a name='app_gradle'>Project/app/build.gradle</a>


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

    signingConfigs {
        release {
            storeFile file("chiemy.jks")
            storePassword "chiemy1989"
            keyAlias "chiemytest"
            keyPassword "chiemy1989"
        }
    }

    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
            buildConfigField "String", "API_URL", "\"http://release.example.com/api\""
        }

        debug {
            applicationIdSuffix ".debug"
            versionNameSuffix "_debug"
            signingConfig signingConfigs.release
            buildConfigField "String", "API_URL", "\"http://debug.example.com/api\""
            resValue "String", "app_name", "Example DEBUG"
        }

        custom1 {
            applicationIdSuffix ".custom1"
        }
    }

    productFlavors{

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

大致的结构是这样的

```
apply plugin: 、、、
android {
	compileSdkVersion 、、、
    buildToolsVersion 、、、
    // 默认配置信息
	defaultConfig{
		、、、
	}
	// 签名配置信息
	signingConfigs{
		// 名称 + {}为一个配置，可有多个
		// 默认有一个名为debug的签名配置
		configName{
			、、、
		}
	}
	// 构建类型
	buildTypes{
		// 构建类型名称 + {} 为一个构建类型，可有多个
		// 默认有一个名为debug的配置
		buildTypesName{
		}
		、、、
	}
	// 定制产品，一般用于多渠道
	productFlavors{
		// 定制名 + {} 为一个定制版，可有多个
		flavorName{
		}
		、、、
    }
}
// 依赖
dependencies {
}
```

处于某个模块下的 build.gradle 文件只对当前模块起作用。所以这个文件只对app这个模块起作用。

```
apply plugin: 'com.android.application'
```

这里引入了一个插件，插件用于扩展 gradle 脚本的能力，在一个项目中使用插件，这样该项目的构建脚本就可以使用该插件定义好的属性和它的 tasks。

此插件是 google 的 Android 开发团队编写的插件，能够提供所有关于 Android 应用和依赖库的构建，打包和测试。

> 注意：当你在开发一个依赖库，那么你应该用 'com.android.library'，并且你不能同时使用他们2个，这将导致构建失败，一个模块要么使用 Android application 或者 Android library 插件，而不是二者。

下面详细说下各 `{}` 的作用及配置：

### android{}

> 注：`android{}` 下的方法和属性，可以通过此链接查询 > [Android Plugin DSL Reference](http://google.github.io/android-gradle-dsl/current/index.html)

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

在 gradle 被用来作为 Android 构建工具之前，AndroidManifest.xml 文件中的 package name 有两个作用：1、app 的唯一标识，2、R 资源文件的包名(有时也是源码的包名)

为了方便的构建不同版本的应用，Android 开发团队将 package name 的两大功能拆分开，AndroidManifest 文件中定义的 package name 依然被用来作为包名和 R 文件的包名。而 applicationid 将被用在设备和各大应用商店中作为唯一的标示。

`minSdkVersion`、`targetSdkVersion`、`versionCode`、`versionName` 将会覆盖 AndroidManifest 中的相应信息，所以没必要在 AndroidManifest 中定义这些属性了。

#### signingConfigs{} 签名配置

```
signingConfigs {
	release {
		storeFile file("chiemy.jks")
		storePassword "chiemy1989"
		keyAlias "chiemytest"
		keyPassword "chiemy1989"
	}
}
```

签名文件配置，将在 `buildTypes` 中用到。`release{}`，是用于正式版签名的配置，我们可以以配置名 + {}的形式，添加其他自定义的配置。

**storeFile** 签名文件保存路径，这里使用的是相对路径，可以是绝对路径。

**storePassword** 签名文件密码

**keyAlias** 签名key别名

**keyPassword** key密码


#### buildTypes{} 构建类型

```
buildTypes {
        release {
            // 剔除无用资源, 这个是依赖代码混淆的, minifyEnabled也要设置为true
            shrinkResources true
            // 是否优化/混淆
            minifyEnabled true
            // 混淆文件规则文件, proguard-android.txt是默认的混淆设置(在Android SDK tools/proguard/ 文件夹下), proguard-rules.pro'是提供给我们添加混淆规则的
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
            buildConfigField "String", "API_URL", "\"http://release.example.com/api\""
        }

        debug {
            // debug 版包名以.debug结尾, 这样我们的手机上正式版和测试版就可以共存了
            applicationIdSuffix ".debug"
            versionNameSuffix "_debug"
            signingConfig signingConfigs.release
            buildConfigField "String", "API_URL", "\"http://debug.example.com/api\""
            resValue "string", "app_name", "Example DEBUG"
        }

		// 还可以定义更多其他的type，格式为 名称 + {} 的形式
    }
```

**release{}** 正式版的构建配置

**debug{}** debug版构建配置，如果不添加的话，Android Studio也会默认有一个debug的类型。

下表是可能用到的一些属性，及其默认值

<table>
<thead>
<tr>
<th>Property name</th>
<th style="text-align:center">debug 版默认值</th>
<th style="text-align:right">release 或其他版本默认值</th>
</tr>
</thead>
<tbody>
<tr>
<td> <code>debuggable</code></td>
<td style="text-align:center">true</td>
<td style="text-align:right">false</td>
</tr>
<tr>
<td> <code>jniDebugBuild</code></td>
<td style="text-align:center">false</td>
<td style="text-align:right">false</td>
</tr>
<tr>
<td> <code>renderscriptDebugBuild</code></td>
<td style="text-align:center">false</td>
<td style="text-align:right">false</td>
</tr>
<tr>
<td> <code>renderscriptOptimLevel</code></td>
<td style="text-align:center">3</td>
<td style="text-align:right">3</td>
</tr>
<tr>
<td> <code>applicationIdSuffix</code></td>
<td style="text-align:center">null</td>
<td style="text-align:right">null</td>
</tr>
<tr>
<td> <code>versionNameSuffix</code></td>
<td style="text-align:center">null</td>
<td style="text-align:right">null</td>
</tr>
<tr>
<td> <code>signingConfig</code></td>
<td style="text-align:center">android.signingConfigs.debug</td>
<td style="text-align:right">null</td>
</tr>
<tr>
<td> <code>zipAlign</code></td>
<td style="text-align:center">false</td>
<td style="text-align:right">true</td>
</tr>
<tr>
<td> <code>runProguard</code></td>
<td style="text-align:center">false</td>
<td style="text-align:right">false</td>
</tr>
<tr>
<td> <code>proguardFile</code></td>
<td style="text-align:center">N/A (set only)</td>
<td style="text-align:right">N/A (set only)</td>
</tr>
<tr>
<td> <code>proguardFiles</code></td>
<td style="text-align:center">N/A (set only)</td>
<td style="text-align:right">N/A (set only)</td>
</tr>
</tbody>
</table>

**minifyEnabled**

是否剔除无用代码，混淆、优化代码。在你设置为 true 时，一定要明确哪些类、方法、属性不需要混淆，因为有些代码和第三方库中的代码是不能混淆的，需要添加混淆规则，否则会出现问题。有些第三方库在文档里有明确的混淆说明，这块一定要留意，更多详细内容见 [shrinkResources](#shrink) 下的链接。

**proguardFiles**

根据那些文件规则进行混淆, `proguard-android.txt` 是默认的混淆设置(在Android SDK tools/proguard/ 文件夹下), `proguard-rules.pro` 是提供给我们添加混淆规则的

<a name='shrink'>**shrinkResources**</a>

是否压缩资源（剔除无用的资源文件，除values外），这个要配合 `minifyEnabled` 使用，如果 `shrinkResources` 为 true，那么 `minifyEnabled` 也要为 true。

> 关于代码优化、资源压缩，请参考此链接 [Shrink Your Code and Resources](https://developer.android.com/studio/build/shrink-code.html)

**applicationIdSuffix** 

会在`defaultConfig`的`applicationId`的基础上加上一个后缀，这个对不同版本在手机上的共存很有用处。

如上边的 debug 类型里，我添加了 「.debug」 的后缀，那么我在调试时，手机上的软件包名为 `com.chiemy.example.gradlestudy.debug`，对我安装的正式版应用并不会产生影响，两个版本可以共存。

**versionNameSuffix** 

为`versionName`添加后缀。

**signingConfig** 

签名配置，当进行构建时，会按照此配置进行签名。

如果 debug 不配置 signingConfig 的话，也会使用一个默认的签名配置文件，这个文件位于 `$HOME/.android/debug.keystore` 目录下，如果不存在的话，在构建时会自动创建。

在配置文件中，我们的 debug 类型和 release 类型用了同一个签名配置，这对于调试一些必须使用正式签名才能正常运行的第三方SDK（如百度地图 SDK ）来说很有用处。

当然，我们可能不希望把签名密码暴露出来，我们可以采用如下方式 > [Stack Overflow post](http://stackoverflow.com/questions/18328730/how-to-create-a-release-signed-apk-file-using-gradle)

**buildConfigField** 

在配置中，我们还用到了这个属性。格式如下

```
buildConfigField "字段类型", "字段名称", "字段值"
```

配置此属性后，会在相应的 module 下的 `build/generated/source/buildConfig/构建变种名（buildType + buildFlavor）/包名/BuildConfig` 文件内生成相应的属性。

> 注：构建变种（Build Varients） = Build Type + Build Flavor，将在介绍 `productFlavors` 之后说明。

我们对 `BuildConfig` 这个文件并不陌生，经常用到的就是 `BuildConfig.DEBUG` 属性了。以前只知道不要手动去修改此文件，现在知道如何添加属性了。

还有一种应用场景，就是像我配置文件里写的，接口地址可以根据不同的版本进行配置，想想之前的做法简直太low。

其他的应用场景，就自己来发挥想象了。

`BuildConfig` 也受到其他属性的一些影响，以 debug 的 BuildConfig 文件为例，我们看下：

```
public final class BuildConfig {
  // 可通过 debuggable 属性修改
  public static final boolean DEBUG = Boolean.parseBoolean("true");
  // applicationId + applicationIdSuffix
  public static final String APPLICATION_ID = "com.chiemy.example.gradlestudy.debug";
  public static final String BUILD_TYPE = "debug";
  public static final String FLAVOR = "";
  public static final int VERSION_CODE = 1;
  // versionName + versionNameSuffix
  public static final String VERSION_NAME = "1.0_debug";
  // Fields from build type: debug
  public static final String API_URL = "http://debug.example.com/api";
}
```

除了以上属性之外，Build Type 还会受项目源码和资源影响： 对于每一个 Build Type 都会自动创建一个匹配的sourceSet。默认的路径为：`src/<buildtypename>/`，此路径可以被修改。

```
android {
    sourceSets.buildtypename.setRoot('路径')
}
```
该文件夹不会自动为你创建，所有你需要手工创建。在这里我们可以像 `main` 文件夹一样，创建 `java`， `res` 文件夹，`AndroidManifest。xml` 文件。将通过以下方式被使用：

- manifest 将被合并进 main 下的 manifest 中
- java 代码就像另一个源码文件夹一样
- 资源文件将叠加到 main 的资源中，并替换已存在的资源。


#### productFlavors{} 产品定制

```
productFlavors{
	chiemy{
		// 替换manifest中的占位符UMENG_CHANNEL_VALUE 为 own
		manifestPlaceholders= [UMENG_CHANNEL_VALUE : "own"]
	}

	market_360{
		manifestPlaceholders= [UMENG_CHANNEL_VALUE : "360"]
	}

	wandoujia{
		manifestPlaceholders= [UMENG_CHANNEL_VALUE : "wandoujia"]
	}
}
```

和构建版本不同，product flavors 用来为一个 app 创建不同版本。典型的例子是，一个app有付费和免费版。product flavors 极大简化了基于相同的代码构建不同版本的 app。

> 注意：flavor 的命名不能与已存在的 Build Type 或者 androidTest 这个 sourceSet 有冲突。

也许我们对 buildType 和 productFlavor 的区别还是有些模糊，我们什么时候用 buildType ？什么时候应该用 productFlavors ?

从字面看，build type 关注的是build，是开发过程，开发的生命周期；Product flavors 关注的是 Product，最终产品该如何分发。

如果版本的区别在于是内部使用（测试）还是外部使用（面向用户），那么使用 buildType。如果面向最终用户需要多个版本，那么你需要使用 product flavors。

product flavors常见的应用场景：多渠道打包

> build Type 和 product flavor 区别，见[Why are build types distinct from product flavors?](http://stackoverflow.com/questions/27905934/why-are-build-types-distinct-from-product-flavors)

就像构建版本一样，product Flavors 也有自己的代码文件夹，名称为 `flavor名 + buildType`。此文件夹不会自动创建，如果 flavor 版本需要不同的代码，不同的资源，我们可以创建这个文件夹，把相应的代码和资源放到里边。

如在我的项目里，我创建了一个 `chiemyRelease` 的文件夹，这个对应的就是 chiemy 这个 flavor 的 release 版本，我在这里添加了一个图标，这样在构建此版本时，此图标就会替换 main 相同文件夹下的图标，达到自定义版本图标的目的。

#### Build Type + Product Flavor = Build Variant 构建变种

每一种 Build Type 和 Product Flavor 的组合就是一个Build Variant（构建变种版本）。

例如，在上面的 Flavor 声明例子中与默认的 debug 和 release 两个 Build Type 将会生成 6 个 Build Variant ：

<image src="capture/capture06.webp" width=400>

> 注：通过 Android Studio 左边栏下方的 Build Variants 可查看和切换 Build Variant。

项目中如果没有定义flavor同样也会有Build Variant，只是使用的是默认的flavor和配置。default(默认)的flavor/config是没有名字的，所以生成的Build Variant列表看起来就跟Build Type列表一样。

### dependencies{}

这个是 Gradle 的通用方法，不是 android 特有的，所以放在了 android{} 外部。

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

使用 gradle 之后这一切都太爽了，只需要做个依赖声明，依赖包会在你执行 build 构建的时候自动从远程仓库下载。当然 Gradle 会为你在本地保留缓存，所以一个特定版本的依赖包只需要下载一次。（特意说下，如果依赖包都已经下载完，并可运行了，我们最好将 gradle 设置为离线状态「 Preference > Build,Execution,Deployment > Build Tools > Gradle > 勾选 Offline work」，以加快下次构建的速度。在依赖有更新时，我们才取消离线。）

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

Maven 和 Jcenter 仓库是很出名的两大仓库。我们没必要同时使用他们，在这里建议使用 jcenter，jcenter 是 maven 中心库的一个分支，这样你可以任意去切换这两个仓库。当然 jcenter 也支持了 https，而 maven 仓库并没有。

#### 远程非中央仓库

有些组织，创建了一些有意思的插件或者 library，他们更愿意把这些放在自己的 maven 库，而不是 maven 中心库或 jcenter。那么当你需要是要这些仓库的时候，你只需要在 maven 方法中加入 url 地址就好：

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

Android Studio 也是不建议使用动态版本的，如果使用了动态版本，会出现如下警告：

<image src="capture/capture03.webp" width=600>

#### 使用 Android Studio 工具添加依赖库

除了手动添加外，我们可以使用 Android Studio 工具添加依赖，在项目上点击右键 > Open Module Setting > 点击相应的 module > 点击 「Dependencies」 标签

<image src="capture/capture01.webp" width=600>


点击下方的加号，出现以下选项：

<image src="capture/capture02.webp" width=200>

**Library dependency**

这个是添加 maven 仓库远程依赖，我们可以根据关键字在线搜索，添加相应的依赖。

<image src="capture/capture04.webp" width=600>

**File dependency** 我们添加本地的jar包依赖

**Module dependency** 添加 module 作为依赖

通过以上方式，会自动生成相应的配置代码。


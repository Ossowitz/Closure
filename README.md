![Groovy-logo.svg.png](src%2Fmain%2Fresources%2Fphoto%2FGroovy-logo.svg.png)

# Groovy compiler

![Blank diagram.png](src%2Fmain%2Fresources%2Fphoto%2FBlank%20diagram.png)

Когда используется **groovyc** и на вход подаётся *FirstScript.groovy*, в Groovy есть **Parser**, который парсит
*FirstScript.groovy* класс, добавляет AST transformations (что-то вроде аналогии библиотеки Lombok). После чего,
исходный код попадает в **Class generator**, который занимается преобразованием того, что у нас вышло из **Parser** в
обычный байт-код. И результат его работы мы видим на нашем диске - *FirstScript.class* байт-код.

В свою очередь, когда мы **groovy** подаём тот же *FirstScript.groovy*, происходит то же самое: переходит в **Parser**,
парсит *FirstScript.groovy* и передаёт его в **Class generator**, который в свою очередь, преобразовывает в байт-код.
Но в этом случае он не сохраняет результат работы на диск - он хранит их в памяти. И после чего вызывает что-то вроде
эквивалента **java** FirstScript. То есть, программа, которая принимает на вход байт-код и исполняет его.

Естественно, здесь есть нюансы: FirstScript не может так просто запуститься, потому что на выходе мы получаем особенные
классы. Более того, Java должна знать, откуда их брать. То есть, должен быть правильно указан ClassPath и многое другое.
Следовательно, используются специфичные ClassLoader'ы - это GroovyClassLoader'ы, которые и помогают загружать все наши
Groovy-классы, которые были скомпилированы в байт-код.

# ClassLoader. ClassPath

Каждый объект знает класс, к которому он относится.

```groovy
package com.jkrotal

class MainClass {

    static void main(String[] args) {
        String value = "Hello World"
        Class<String> clazz = value.getClass()
    }
}
```

Например, "Hello World" - это всего лишь строка. И эта строка относится к объекту класса String. То есть, это тоже
объект, параметризованный строкой. А Class - это всего лишь обычный класс. Следовательно, мы и говорим такой термин
«объект класса Class». Просто, для класса, который мы создаём, либо используем из Java, мы параметризовываем
соответствующим типом. И также мы знаем, что они синглтоны.

```groovy
package com.jkrotal

class MainClass {

    static void main(String[] args) {
        String value = "Hello World"
        Class<String> clazz = value.getClass()
        assert clazz == String.class
    }
}
```

То есть, если мы сделаем **assert**, то он всегда будет давать **true**.

Как раз-таки, для создания таких объектов класса Class (Class<String>), нам и нужны ClassLoader'ы. То есть, объекты этих
классов занимаются тем, что загружают наши классы в **Metaspace**. И так как они занимаются загрузкой этих классов, то
каждый объект класса Class знает о **ClassLoader**.

И если мы запустим:

```groovy
package com.jkrotal

class MainClass {

    static void main(String[] args) {
        String value = "Hello World"
        Class<String> clazz = value.getClass()
        println clazz.getClassLoader()
    }
}
```

То получим:

```text
null
```

Но если мы запустим:

```groovy
package com.jkrotal

import java.sql.DriverManager

class MainClass {

    static void main(String[] args) {
        println DriverManager.class.getClassLoader()
        println MainClass.class.getClassLoader()
    }
}
```

То мы получим совершенно разные объекты ClassLoader.

```text
jdk.internal.loader.ClassLoaders$PlatformClassLoader@6babf3bf
jdk.internal.loader.ClassLoaders$AppClassLoader@2328c243
```

В первом случае был **null**, во втором - **PlatformClassLoader**, в третьем - **AppClassLoader**.

И последние два - какие-то внутренние классы нашего класса ClassLoaders.

![Blank diagram2.png](src%2Fmain%2Fresources%2Fphoto%2FBlank%20diagram2.png)

**JDK** состоит из двух компонентов: **Dev tools** и **JRE**.

В **Dev tools** находятся различные утилиты: **javac** (для компиляции Java-кода в байт-код), далее **java** (запуск
Java-приложений, то есть, скомпилированных предыдущей утилитой), **jconsole**, **jheap** и так далее. И все они лежат
в каталоге **bin**.

В свою очередь, **JRE** состоит из двух составных элементов: **JVM** и **Java classes**.

**JVM** нужна для того, чтобы мы интерпретировали скопилированный код в машинный.

![Blank diagram3.png](src%2Fmain%2Fresources%2Fphoto%2FBlank%20diagram3.png)

Когда у нас получаются скомпилированные классы на соответствующей платформе, мы используем установленный нам **JRE**,
в котором есть **JVM**. И она во время выполнения (то есть, в Runtime) транслирует в машинный код.

Естественно, Java Classes, которые идут с установленным JDK, как раз нужны для написания наших программ. То есть, это
java.lang, java.stream, java.util и так далее. И для загрузки наших классов из Java Core, нам нужны ClassLoaders.

И по умолчанию, есть три основных ClassLoaders: **Bootstrap ClassLoader**, **Platform ClassLoader**, **App Classloader**
*(System)*.

![Blank diagram4.png](src%2Fmain%2Fresources%2Fphoto%2FBlank%20diagram4.png)

И как раз, **Bootstrap ClassLoader** нужен для загрузки критически важных классов в первую очередь. То есть, он уже
встроен в **JVM** и в нём находятся такие классы, как класс **ClassLoader** (который загружает другие классы), всё что
находится в Java-base модуле и многие другие.

**Platform ClassLoader** загружает модули, которые не так критически важны. И он загружает их только во время
требований. Так сделали, чтобы ускорить старт java приложений: Bootstrap классы - сразу же загружаются в память,
остальные классы - lazy, по требованию.

Из примера, **DriverManager** был загружен нашим Platform ClassLoader.

**App ClassLoader (System)** нужен для загрузки классов, которые мы сами написали, и некоторых пакетов из java модулей.

Выведенный null - это как раз наш Bootstrap ClassLoader.

Между загрузчиками классов существует **parent-child зависимость**. **Bootstrap** - parent всех остальных ClassLoaders.
Platform зависит от Bootstrap ClassLoader. А App ClassLoader - child для нашего Platform ClassLoader.

И когда мы загружаем класс, сначала мы должны проверить у всех наших parent. То есть, сначала проверяется класс у
Bootstrap ClassLoader. Если там не нашлись классы, следовательно, скорее всего, Platform ClassLoader загрузил их. Если
мы и там не нашли, то мы ищем в App ClassLoader и так далее. Например, мы можем написать свой ClassLoader. Например, так
поступает Tomcat для того, чтобы загружать классы из папки webapp, где мы разворачиваем наши war.

Для загрузки собственных классов используется переменная **-classpath**. **AppClassLoader** загружает все классы из
**-classpath**. То есть, указание пути, где искать наши файлы.

```text
"C:\Program Files\JetBrains\IntelliJ IDEA 2022.3.1\jbr\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2022.3.1\lib\idea_rt.jar=60019:C:\Program Files\JetBrains\IntelliJ IDEA 2022.3.1\bin" -Dfile.encoding=UTF-8 -classpath C:\Users\kohdy\IdeaProjects\groovy-gradle-starter\build\classes\groovy\main;C:\Users\kohdy\IdeaProjects\groovy-gradle-starter\build\resources\main;C:\Users\kohdy\.gradle\caches\modules-2\files-2.1\org.apache.groovy\groovy\4.0.14\4d512d413b426037b7027fd39426542748ed4581\groovy-4.0.14.jar com.jkrotal.MainClass
Hello World

Process finished with exit code 0

```

И если мы посмотрим на строку, которую запускаем IntelliJ IDEA, то мы увидим, что она обращается к утилите **java**,
которая запускает наше приложение на JVM.

```text
"C:\Program Files\JetBrains\IntelliJ IDEA 2022.3.1\jbr\bin\java.exe"
```

И здесь мы можем увидеть интересный флаг **-classpath**.

```text
-classpath C:\Users\kohdy\IdeaProjects\groovy-gradle-starter\build\classes\groovy\main;C:\Users\kohdy\IdeaProjects\groovy-gradle-starter\build\resources\main;C:\Users\kohdy\.gradle\caches\modules-2\files-2.1\org.apache.groovy\groovy\4.0.14\4d512d413b426037b7027fd39426542748ed4581\groovy-4.0.14.jar com.jkrotal.MainClass
```

То есть, это обычный аргумент, который передаётся при вызове утилиты **java**. И в нём содержатся пути ко всем классам,
о которым мы хотим, чтобы JVM знала. То есть, здесь находятся сторонние JAR-файлы, любые другие пакеты. После того как 
мы указали JAR-файлы, мы видим запуск нашего MainClass.

**classpath** используется для загрузки сторонних библиотек, JAR-файлов и классов, которые не идут вместе с поставкой 
JRE. 

Классы можно загружать вручную через Class.forName("className"). Если класса не оказалось в classpath, будет брошено 
исключение: ClassNotFoundException.
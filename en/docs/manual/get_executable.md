# Getting Executables

Users can choose any of the following methods to get FISCO BCOS executable. It is recommended to download the precompiled binaries from GitHub.

- The official statically linked precompiled files can be used on Ubuntu 16.04 and CentOS 7.2 version or later.
- docker image is provided officially, welcome to use. [docker-hub address](https://hub.docker.com/r/fiscoorg/fiscobcos)
- You can compile from the source code, visit here source code compilation.

## Downloading precompiled fisco-bcos

The statically linked precompiled executable provided has been tested on Ubuntu 16.04 and CentOS 7. Please download the latest released **pre-compiled executable** from the [Release](https://github.com/FISCO-BCOS/FISCO-BCOS/releases).

## docker image

From v2.0.0 version, we provide the docker image for the tag version. Corresponding to the master branch, we provide image of `latest` tag. For more docker tags please refer to[here](https://hub.docker.com/r/fiscoorg/fiscobcos/tags).

build_chain.sh script adds the `-d` option to provide docker mode building for developers to deploy. For details, please refer to [here](build_chain.html#id4).

```eval_rst
.. note::
    For using build_chain.sh script easily, we start docker by using ``--network=host`` network mode. Users may need to customize and modify according to their own network scenarios when they actually use.

```

## Source code compilation

```eval_rst
.. note::

    The source code compilation is suitable for the experienced developers. You are required to download all library dependencies during compilation. Network connection would required and would take 5-20 minutes in total.
```

FISCO BCOS is using generic [CMake](https://cmake.org) to generate platform-specific build files, which means the steps are similar for most operating systems:
1.	Install build tools and dependent package (depends on platform).
2.	Clone code from [FISCO BCOS][FSICO-BCOS-GitHub].
3.	Run `cmake` to generate the build file and compile.

### Installation dependencies

- Ubuntu

Ubuntu 16.04 or later is recommended. The versions below 16.04 have not been tested. You will require to have the build tools build tools and `libssl` for compiling the source code.

```bash
$ sudo apt install -y g++ libssl-dev openssl cmake git build-essential autoconf texinfo
```

- CentOS

CentOS7 version or later is recommended.

```bash
$ sudo yum install -y epel-release
$ sudo yum install -y openssl-devel openssl cmake3 gcc-c++ git
```

- macOS

xcode10 version and above are recommended. macOS dependent package installation depends on [Homebrew](https://brew.sh/).

```bash
$ brew install openssl git
```

### Code clone

```bash
$ git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

### Compile

After compilation, binary files are located at `FISCO-BCOS/build/bin/fisco-bcos`.

```bash
$ cd FISCO-BCOS
$ git checkout master
$ mkdir -p build && cd build
# please use cmake3 for CentOS
$ cmake ..
#To add -j4 to accelerate compilation by 4 compilation processes
$ make
```

### Compile options

- BUILD_GM，off by default, national cryptography compilation flag. To enable it, use `cmake -DBUILD_GM=on ..`
- TESTS, off by default, unit test compilation flag. To enable it, use `cmake -DTESTS=on ..`
- DEMO，off by default, test program compilation switch. To open it through `cmake -DDEMO=on ..`.
- BUILD_STATIC，off by default, static compilation switch, only supports Ubuntu. To open it through `cmake -DBUILD_STATIC=on ..`.
- Generate source documentation.

    ```bash
    # Install Doxygen
    $ sudo apt install -y doxygen graphviz
    # Generate source documentation locate at build/doc
    $ make doc
    ```

[FSICO-BCOS-GitHub]:https://github.com/FISCO-BCOS/FISCO-BCOS

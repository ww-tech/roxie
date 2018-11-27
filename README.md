# Roxie

[![CircleCI](https://circleci.com/gh/ww-tech/roxie/tree/master.svg?style=shield&circle-token=86ceda91392b3e6720cc5fe553c90eec36195b03)](https://circleci.com/gh/ww-tech/roxie/tree/master)

Roxie is a lightweight Android library for building reactive apps. We successfully use it for Android product development at WW.

## Why Roxie?

Roxie implements Unidirectional Data Flow (UDF) concepts introduced by [Redux](https://redux.js.org/). The core idea is that user Actions get dispatched to a Store (State container) which uses Reducers to transform them into States. In Android world, this design pattern is known as MVI (Model-View-Intent) where Model describes State and Intent describes user interaction.

Roxie is a tiny library (merely 46 methods and 52 lines of code including comments). It is implemented using widely adopted lifecycle Google Architecture Components and RxJava. Hopefully, the small footprint of this library will enable you to fully understand both concepts and implementation details.

## Roxie in a nutshell

The Unidirectional Data Flow with Roxie can be summarized as follows:
1. User interaction events (Actions) get dispatched to ViewModel via a single pipeline.
2. Each Action gets transformed into Changes.
3. Each Change combined with a previous State produces a new State using a Reducer.
4. UI observes new States via a single pipeline and renders them as they come in.

Some of Roxie's strengths are:
* State Machine managing immutable States makes data predictable and easier to manage.
* Support for state restoration after device rotation and process death.
* A set of Actions, Changes, and States for each screen result in a thorough user-centric design.
* Logging of Actions and States makes both debugging and crash reporting extremely efficient.
* Rich RxJava APIs help achieve composable functional code.
* Meaningful and consistent unit tests asserting that given Actions and initial State produce correct new States.

## Documentation 

Check out the sample bundled and [wiki pages]( https://github.com/ww-tech/roxie/wiki) to get started. Some of the topics covered are:

* [ViewModel](https://github.com/ww-tech/roxie/wiki/1.-ViewModel)
* [Actions](https://github.com/ww-tech/roxie/wiki/2.-Actions)
* [Changes](https://github.com/ww-tech/roxie/wiki/3.-Changes)
* [States](https://github.com/ww-tech/roxie/wiki/4.-States)
* [Reducer](https://github.com/ww-tech/roxie/wiki/5.-Reducer)
* [Rendering State](https://github.com/ww-tech/roxie/wiki/6.-Rendering-State)
* [Logging](https://github.com/ww-tech/roxie/wiki/7.-Logging)
* [Process Death](https://github.com/ww-tech/roxie/wiki/8.-Process-Death)
* [Unit Tests](https://github.com/ww-tech/roxie/wiki/9.-Unit-tests)

## Installation

Add the following Gradle dependency to your project `build.gradle` file:

```groovy
dependencies {
    implementation 'com.ww.roxie:roxie:0.1.0'
}
```

To control logging, check out [documentation](https://github.com/ww-tech/roxie/wiki/7.-Logging).

## License

    Copyright 2018 WW International, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

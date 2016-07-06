# Change Log

## [1.3.4](https://github.com/jbachorik/btrace/tree/v1.3.4)

[Full Changelog](https://github.com/jbachorik/btrace/compare/v1.3.3...v1.3.3)

**Implemented enhancements:**

- Allow AnyType for arguments annotated by @Return [\#176](https://github.com/jbachorik/btrace/issues/176)
- Instrumentation levels [\#170](https://github.com/jbachorik/btrace/pull/170) ([jbachorik](https://github.com/jbachorik))

**Fixed bugs:**

- @Location\(Kind.NEW\) does not work with parameter annotated by @Return [\#183](https://github.com/jbachorik/btrace/issues/183)
- MethodInvocationRecorder may provide incomplete data in highly concurrent environment [\#180](https://github.com/jbachorik/btrace/issues/180)
- RuntimeException while trying to printfield values [\#179](https://github.com/jbachorik/btrace/issues/179)
- Problems with @Return parameter [\#174](https://github.com/jbachorik/btrace/issues/174)
- Location\(kind = Kind.NEWARRAY\) should support flexible "clazz" definition [\#173](https://github.com/jbachorik/btrace/issues/173)
- Submitting compiled trace script via 'btrace' may not work [\#164](https://github.com/jbachorik/btrace/issues/164)
- Inconsistent behavior when trying to probe return values from multiple methods [\#145](https://github.com/jbachorik/btrace/issues/145)

**Closed issues:**

- BTrace is crashing the applications that is attached with [\#177](https://github.com/jbachorik/btrace/issues/177)
- SIGSEGV in the main process [\#172](https://github.com/jbachorik/btrace/issues/172)
- how to run multiple btrace scripts for the same application. [\#171](https://github.com/jbachorik/btrace/issues/171)
- how to trace a application which is run by using a tcserver which runs tomcat inbuilt [\#169](https://github.com/jbachorik/btrace/issues/169)
- null pointer exception and illegalstateexception while tracing an application [\#168](https://github.com/jbachorik/btrace/issues/168)
- connection refused or well-known file is not secure [\#167](https://github.com/jbachorik/btrace/issues/167)
- unable to open socket file: target process not responding or hotspot vm not loaded [\#166](https://github.com/jbachorik/btrace/issues/166)
- default trace ouput file created by btracer command is not generating when used in linux terminal [\#165](https://github.com/jbachorik/btrace/issues/165)
- @OnExit not handled properly after \#154 [\#161](https://github.com/jbachorik/btrace/issues/161)
- Omitting the 'method' argument in @OnMethod counter-intuitively performs match against the handler method name [\#159](https://github.com/jbachorik/btrace/issues/159)
- Tracing all methods of all available classes can make BTrace crash [\#154](https://github.com/jbachorik/btrace/issues/154)
- @Duration annotation refused for Kind.CALL [\#153](https://github.com/jbachorik/btrace/issues/153)
- @OnError requires full Throwable type in the handler method signature [\#152](https://github.com/jbachorik/btrace/issues/152)
- default trace ouput file created by btracer command is not generating when used in linux terminal [\#151](https://github.com/jbachorik/btrace/issues/151)
- tracing a \*.jar files from command prompt [\#150](https://github.com/jbachorik/btrace/issues/150)
- Unable to open socket file: target process not responding or HotSpot VM not loaded [\#139](https://github.com/jbachorik/btrace/issues/139)

**Merged pull requests:**

- Using JCTools queues in performance critical code [\#178](https://github.com/jbachorik/btrace/pull/178) ([jbachorik](https://github.com/jbachorik))
- Btrace output beautified [\#147](https://github.com/jbachorik/btrace/pull/147) ([mfilser](https://github.com/mfilser))


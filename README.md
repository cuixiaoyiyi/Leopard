# Leopard

A lightweight tool to detect thread related misuses in Java.

In the Java platform, it is common to use the fundamental asynchronous thread (java.lang.Thread). However, due to garbage collection and thread interruption mechanism, it can be easily misused. The recycle of an object will be blocked if an alive thread holds a strong reference to it. In addition, the careless implementation of asynchronous thread may cause that there is not responding to the interrupt mechanism. This may result the unexpected thread related behavior and resource leak/waste.

we implement a lightweight tool named Leopard to detect them statically, which is a very fast one with the demand-driven slicing analysis. It reduces false negatives caused by the heavyweight time-consuming path sensitive analysis proposed by existing work.

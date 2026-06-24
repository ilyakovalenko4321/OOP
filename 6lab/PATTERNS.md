# Patterns used in Lab 6

## Factory Method

The shape hierarchy is created through `ShapeFactory` implementations. The UI works with the common factory interface and does not need to know constructors of `Line`, `Rectangle`, plugin `Star`, or other shapes. This is appropriate because new classes in the hierarchy can be added by registering or loading a new factory.

## Adapter

`FriendUppercaseToolAdapter` wraps a simulated plugin from another project. The friend's class has methods named `packForFriendApp` and `unpackFromFriendApp`, while this application expects `XmlProcessingPlugin.beforeSave` and `XmlProcessingPlugin.afterLoad`. The adapter translates the foreign API to the local plugin contract, so the settings menu can load it like a normal functional plugin.


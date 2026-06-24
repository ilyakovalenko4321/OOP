# Lab 6: Adapter and patterns

## What was done

- The project is based on `5lab`.
- Native XML processing plugins from lab 5 are preserved.
- A simulated friend's plugin was added in `FriendUppercaseToolAdapter`.
- The friend's API is incompatible with this project, so it is connected through the Adapter pattern.
- Two patterns are described in `PATTERNS.md`: Factory Method and Adapter.

## How it works

1. `ProcessingPluginLoader` loads all `.class` files from `processors`.
2. `FriendUppercaseToolAdapter` implements the local `XmlProcessingPlugin` interface.
3. Inside the adapter, calls are delegated to the friend's methods.
4. The adapted plugin appears in `Settings` together with native plugins.

## Build and run

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
javac -cp out -d plugins (Get-ChildItem -Recurse plugin-src -Filter *.java).FullName
javac -cp out -d processors (Get-ChildItem -Recurse processing-src -Filter *.java).FullName
java -cp "out;plugins;processors" Main
```

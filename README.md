# ⚔️ Table Top Role Playing Game Combat Simulator

A combat simulator using 5e SRD mechanics, supporting both SRD 5.1 and SRD 5.2.1 (CC-BY-4.0).

![Screenshot showing a game in progress](docs/screenshot.png)

# Getting Started

## Get all tasks

```bash
./gradlew tasks
```

## Build all targets

```bash
./gradlew clean assemble
```

## Run JVM Target

Import project as Gradle project into your IDE and start from the main class in module `jvmMain`.

## Run JS Development Server

```bash
./gradlew jsBrowserDevelopmentRun
```

## Build JS Development Webpack Bundle

```bash
./gradlew jsBrowserDevelopmentWebpack
```

## Run WASM Development Server

```bash
./gradlew wasmJsBrowserDevelopmentRun
```

## Build WASM Development Webpack Bundle

```bash
./gradlew wasmJsBrowserDevelopmentWebpack
```



---

## 📜 Licensing & Legal

This project uses rules / data from:

- **SRD 5.1** — released under the *Creative Commons Attribution 4.0 International (CC-BY-4.0)* license.
- **SRD 5.2.1** — released under the *Creative Commons Attribution 4.0 International (CC-BY-4.0)* license.

---

## ⚠️ Disclaimer

This project is **not affiliated with** or **endorsed by** Wizards of the Coast.
“Dungeons & Dragons”, “D&D”, and related names/trademarks are the property
of Wizards of the Coast.

Only content from SRD 5.1 and SRD 5.2.1 is used. No Product Identity
(official settings, unique monsters, named characters, etc.) is included.

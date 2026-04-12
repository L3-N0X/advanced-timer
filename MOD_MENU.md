Yes — in a multiloader project, the clean setup is to keep one shared YACL screen builder in your common code and add a thin loader-specific hook for each platform. On NeoForge, the official way to make the Mods page show a working Config button is to register an `IConfigScreenFactory`, and NeoForge explicitly supports swapping in your own custom screen instead of the default one. [docs.neoforged](https://docs.neoforged.net/toolchain/docs/plugins/mdg/)

## Shared code

In multi-loader projects, the cross-loader/common module is where shared Minecraft-facing code usually lives, while loader-specific registration stays in the platform subprojects. [docs.neoforged](https://docs.neoforged.net/toolchain/docs/plugins/mdg/)
That means your config model and the method that builds the YACL screen should live in common, and Fabric/NeoForge should only call into that method. [boxtoplay](https://www.boxtoplay.com/en-pg/minecraft-hosting/minecraft-server/modrinth-mods/yacl-19816)

```java
// common
public final class MyConfigScreen {
    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            // categories, options, bindings, save action...
            .build()
            .generateScreen(parent);
    }
}
```

## Fabric Mod Menu

Mod Menu is commonly added only to the Fabric side of a multiloader project, while YACL is what exposes an in-game settings UI for supported mods. [youtube](https://www.youtube.com/watch?v=xiLy6xH4p-o)
For Fabric, create a client-only Mod Menu integration class that returns your shared YACL screen factory. [mcmodhub](https://mcmodhub.com/mod-menu/)

```java
// fabric
public final class MyModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MyConfigScreen.create(parent);
    }
}
```

And register that class in `fabric.mod.json`:

```json
{
  "entrypoints": {
    "modmenu": [
      "com.example.mymod.fabric.MyModMenuIntegration"
    ]
  }
}
```

## NeoForge Mods menu

NeoForge’s docs say a config screen is registered through `IConfigScreenFactory`, and that screen becomes available from the in-game Mods page via the Config button. [docs.neoforged](https://docs.neoforged.net/docs/1.21.11/misc/config/)
NeoForge also allows you to provide your own custom screen there, so you can point it at the same shared YACL screen instead of using NeoForge’s default generated config UI. [docs.neoforged](https://docs.neoforged.net/docs/1.21.11/misc/config/)

```java
// neoforge
public final class MyModNeoForge {
    public MyModNeoForge(ModContainer container) {
        container.registerExtensionPoint(
            IConfigScreenFactory.class,
            (modContainer, parent) -> MyConfigScreen.create(parent)
        );
    }
}
```

## Dependency layout

YACL supports modern loaders including Fabric and NeoForge, so using the same screen-building library across both sides is a normal setup. [boxtoplay](https://www.boxtoplay.com/en-pg/minecraft-hosting/minecraft-server/modrinth-mods/yacl-19816)
In practice, put YACL where your shared screen code can compile, and keep Mod Menu itself as a Fabric-only dependency because the integration point is different on NeoForge. [youtube](https://www.youtube.com/watch?v=xiLy6xH4p-o)

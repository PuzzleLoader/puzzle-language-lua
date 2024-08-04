# Puzzle Language Lua
> This is a **lua** language provider for [Puzzle Loader](https://github.com/PuzzleLoader/PuzzleLoader)

## How to use
Checkout [Example Mod Lua](https://github.com/PuzzleLoader/) for help

### How to add as a dependency

Step 1: Add [CRModder's Maven](https://maven.crmodders.dev/) in your build.gradle at the end of your repositories tag.\
Here is an example `repositories` section
```
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

Step 2: Add Flux to your dependencies using the text below
```
gameMod "dev.crmodders:puzzle-language-lua:1.0.0"
```

### Adapter

Use the `lua` adapter for your mod by setting the `adapter` property in the `puzzle.mod.json` file.
Remember to the add a dependency entry to your `puzzle.mod.json` file:

```json
{
    "entrypoints": {
        "main": [
            {
                "adapter": "lua",
                "value": "path/to/file/file.lua"
            }
        ]
    },
    "depends": {
        "puzzle-language-lua": ">=1.0.0"
    }
}
```

## How to test/build
For testing in the dev env, you can use the `gradle runLoader` task

For building, the usual `gradle buildBundleJar` task can be used. The output will be in the `build/libs/` folder

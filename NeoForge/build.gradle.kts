plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    modApi(sharedLibs.puzzleslib.neoforge)
    modApi(sharedLibs.multiloaderdataextensions.neoforge)
    include(sharedLibs.multiloaderdataextensions.neoforge)
}

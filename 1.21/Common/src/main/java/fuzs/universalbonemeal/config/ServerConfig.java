package fuzs.universalbonemeal.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.universalbonemeal.handler.UseBoneMealHandler;

public class ServerConfig implements ConfigCore {
    @Config(description = "Allow bone meal to work on cactus.")
    public boolean allowCactus = true;
    @Config(description = "Allow bone meal to work on sugar canes.")
    public boolean allowSugarCane = true;
    @Config(description = "Allow bone meal to work on all kinds of vines.")
    public boolean allowVines = true;
    @Config(description = "Allow bone meal to work on nether warts.")
    public boolean allowNetherWart = true;
    @Config(description = "Allow bone meal to work on fully grown stems to force producing a melon or pumpkin block.")
    public boolean allowFruitStems = true;
    @Config(description = "Allow bone meal to work on lily pads making them spread around on water.")
    public boolean allowLilyPad = true;
    @Config(description = "Allow bone meal to work on dead bushes making them spread around.")
    public boolean allowDeadBush = true;
    @Config(description = "Allow bone meal to work on small flowers making them spread around.")
    public boolean allowSmallFlowers = true;
    @Config(description = {"Allow bone meal to work on coral turning it into coral reef tree structures for farming coral blocks.", "This does only work when inside of a warm ocean biome and requires a fair bit of open space above the coral."})
    public boolean allowCorals = true;
    @Config(description = {"Allow bone meal to work on chorus plants and flowers.", "The algorithm searches upwards for connected chorus flowers and has a range limit of 48 blocks."})
    public boolean allowChorus = true;
    @Config(description = "Allow bone meal to work on mycelium blocks, spawning various types of mushrooms in the vicinity.")
    public boolean allowMycelium = true;
    @Config(description = "Allow bone meal to work on all kinds of dirt blocks for converting into a grass block or mycelium when such a block is right next to it.")
    public boolean allowDirt = true;
    @Config(description = "Allow bone meal to work on podzol for creating ferns and sweet berry bushes.")
    public boolean allowPodzol = true;
    @Config(description = "Allow bone meal to work on spore blossoms for dropping an additional spore blossom per bone meal used.")
    public boolean allowSporeBlossom = true;

    @Override
    public void afterConfigReload() {
        UseBoneMealHandler.invalidate();
    }
}

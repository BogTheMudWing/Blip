# Blip

Instantly get the item you need.

## About

Blip is not yet but may one day become a Fabric mod that allows you to quickly put whatever you want in your hand.
Here's how it works:

The Blip Box is activated by a configurable keybind (default C), which activates a text box. When you type into the text
box, Blip uses your query to search for items. In Creative mode, this is all available items in the game. In Survival
mode, only items in your inventory are shown. When you press Enter (or return, on Mac), the top result in placed in your
hand. You will be given a single item if in Creative mode. If in Survival mode, the largest stack will be moved into
your hand, and whatever was in your hand will move to where the selected item stack was.

You may optionally add a space after the query and type a number to specify the number of items to put in your hand. For
example, the query **carr 16** would give you 16 carrots.

## Installation

This mod is not yet ready for use, but if you really want to try it, you can close the repository and build it yourself.
You can run the command `./gradlew runClient` to build the mod and test it in-game.

If you're feeling bold, you can download the latest nightly build
[here](https://nightly.link/BogTheMudWing/Blip/workflows/build/main/Artifacts.zip). **This is the active development
version, not a stable release.**

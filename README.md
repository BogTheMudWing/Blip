# Blip

A fast and easy fuzzy-search item grabber.

![Blip gives you a search box that lets you fuzzy find items](repo/images/Blip%20Preview.png)

## About

Blip is a Fabric mod that allows you to quickly put any item in your hand. It is designed to be quick and easy to use.

Click the image below to go to YouTube to watch a video of Blip in action.

[![Blip Demo](https://i.ytimg.com/vi/kvs01urkPoA/maxresdefault.jpg?sqp=-oaymwEmCIAKENAF8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGFsgZSg3MA8=&rs=AOn4CLC1kRDzoviQVTw8SefdISynWkYEug)](https://www.youtube.com/watch?v=kvs01urkPoA)

Click the image below to go to YouTube to watch a video explainging Blip's functionality.

[![Blip Showcase](https://i.ytimg.com/vi/V03D6UsprnQ/maxresdefault.jpg?sqp=-oaymwEmCIAKENAF8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGGUgQShOMA8=&rs=AOn4CLBKl-ZxqYNijPD8tshGWsNGyRKPfQ)](https://www.youtube.com/watch?v=V03D6UsprnQ)

Here's how it works:

The search is activated by a configurable keybind (default J), which activates a text box. When you type into the text
box, Blip uses your query to search for items. When you press Enter (or return, on Mac), the top result is placed in your
hand. You will be given a single item if in Creative mode. If in Survival mode, the first stack will be moved into
your hand, and whatever was in your hand will move to where the selected item stack was.

You may optionally specify a number before or after the query separated by a space to specify the number of items to put
in your hand. For example, the queries **carr 16** or **16 carr** would give you 16 carrots.

## Installation

You need to install the Fabric Mod Loader. See <https://fabricmc.net>.

Download the latest release from [GitHub](https://github.com/BogTheMudWing/Blip/releases) or
[Modrinth](https://modrinth.com/mod/Blip).

To install the mod, follow this guide: <https://docs.fabricmc.net/players/installing-mods>.

## Uninstallation

Remove `blip-*.jar` from the mods folder.

Blip does not generate or modify any other files.

## Nightly

If you're feeling bold, you can download the latest nightly build
[here](https://nightly.link/BogTheMudWing/Blip/workflows/build/main/Artifacts.zip). **This is the active development
version, not a stable release. It may not work at all.**

## Contributing

There are many ways in which you can contribute:

- For bug reports, please create an issue on the GitHub repository detailing exactly how the error occurred and any relevant
crash reports or logs.
- For feature requests, please create an issue on the GitHub repository detailing what the feature or enhancement should be
and how it would improve the project.
- For pull requests, please maintain good coding practices and take feedback eagerly.
- You can also contribute simply by sharing the mod with other people who might like it :)

## Thanks to

- Mojang for developing an awesome game.
- The FabricMC project for creating Minecraft modding frameworks.
- GitHub, for hosting my code.
- Apache, for the fuzzy search system FuzzyScore.

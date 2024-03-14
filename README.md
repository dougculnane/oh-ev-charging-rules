# oh-ev-charging-rules

openHAB EV Charging Rules are complex rules that optimise electric vehicle charging. Using openHAB items for interaction with physical devices, via openHAB bindings, jRules handle the complexity of smart charging control. A UI Widget handles user interaction.

![Page](/docs/Page.png)

Rules can be enabled and configured.  Individual cars can have charging target/limits set.

![Page](/docs/PageWithSettings.png)

## Prerequisites

* openHAB runtime.
* jRules automation addon.
* ev charger and configured openHAB binging.

## Recomended

* EV and configured openHAB binding.
* Items for electic price linked via energy supplier bindings or set with openHAB rules.


## Installation.

* Add openHAB items from files in items/
* Add openHAB widget from files in widgets/
* Add openHAB pages from files in pages/
* Link evcr items to openHAB thing channels.
* Copy rules jar to jrules-jar folder.

## Developer HowTo

Clone build and deploy this project. A docker testing runtime is detailed in src/test/docker

```bash
mvn clean install
docker cp target/oh-ev-charging-rules-1.0-SNAPSHOT.jar openhab:/openhab/conf/automation/jrule/rules-jar/
```

PRs are welcome.

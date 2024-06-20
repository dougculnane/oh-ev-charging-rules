# oh-ev-charging-rules

openHAB EV Charging Rules are complex rules that optimise electric vehicle charging. Using openHAB items for interaction with physical devices, via openHAB bindings, jRules handle the complexity of smart charging control. A UI Widget handles user interaction.

![Page](/docs/Page.png)

Rules can be enabled and configured.  Individual cars can have charging target/limits set.

![Page](/docs/PageWithSettings.png)

## Prerequisites

* openHAB runtime.
* jRules automation addon.
* ev charger and configured openHAB binding.

## Recomended

* EV and configured openHAB binding.
* Items for electic price linked via energy supplier bindings or set with openHAB rules.

## Installation.

* Add openHAB items from files in items/
* Add openHAB widget from files in widgets/
* Add openHAB widget to a page
* Link evcr items to openHAB thing channels.
* Copy rules jar to jrules-jar folder.

## Developer HowTo

Clone build and deploy this project. A docker testing runtime is detailed in src/test/docker

```bash
mvn clean install
docker cp target/oh-ev-charging-rules-1.0-SNAPSHOT.jar openhab:/openhab/conf/automation/jrule/rules-jar/
```

Tested implementations of Chargers and PRs are welcome.

## Release Notes

### Version 1.1.0

* Polling reduced.  Item change events used to trigger decisions.
* evcr_action_taken_switch new switch to indicate if action was taken.

### Version 1.0.1

* New feature use exported power with 1 phase only

### Version 1.0.0

* Real world testing and fixing for GoeCharger_API2.
* Feature complete implementation of rules.
* Some unit testing of rules.

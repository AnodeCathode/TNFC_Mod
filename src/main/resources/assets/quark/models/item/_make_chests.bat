@echo off
:: Vazkii's JSON creator for blocks
:: Put in your /resources/assets/%modid%/models/block
:: Makes basic block JSON files as well as the associated item and simple blockstate
:: Can make multiple blocks at once
::
:: Usage:
:: _make (block name 1) (block name 2) (block name x)
::
:: Change this to your mod's ID
set modid=quark

setlocal enabledelayedexpansion

for %%x in (%*) do (
	echo Making %%x.json chest
	(
		echo {
		echo 	"parent": "%modid%:block/custom_chest",
		echo 	"textures": {
		echo 		"texture": "%modid%:blocks/chests/%%x",
		echo 		"particle": "%modid%:blocks/chests/%%x"
		echo 	}
		echo }
	) > custom_chest_%%x.json

	echo Making %%x.json chest_trap
	(
		echo {
		echo 	"parent": "%modid%:block/custom_chest",
		echo 	"textures": {
		echo 		"texture": "%modid%:blocks/chests/%%x",
		echo 		"particle": "%modid%:blocks/chests/%%x",
		echo 		"overlay": "%modid%:blocks/chests/trap"
		echo 	}
		echo }
	) > custom_chest_trap_%%x.json

)

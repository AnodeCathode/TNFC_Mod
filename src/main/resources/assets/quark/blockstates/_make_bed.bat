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

	echo Making colored_bed_%%x.json
	(
		echo {
    	echo 	"variants": {
    	echo		"inventory": { "model": "quark:colored_bed_foot" },
        echo 		"facing=north,part=foot": { "model": "quark:colored_bed_foot", "y": 180 },
        echo 		"facing=east,part=foot":  { "model": "quark:colored_bed_foot", "y": 270 },
        echo 		"facing=south,part=foot": { "model": "quark:colored_bed_foot" },
        echo 		"facing=west,part=foot":  { "model": "quark:colored_bed_foot", "y": 90 },
        echo 		"facing=north,part=head": { "model": "quark:colored_bed_head", "y": 180 },
        echo 		"facing=east,part=head":  { "model": "quark:colored_bed_head", "y": 270 },
        echo 		"facing=south,part=head": { "model": "quark:colored_bed_head" },
        echo 		"facing=west,part=head":  { "model": "quark:colored_bed_head", "y": 90 }
    	echo 	}
    	echo }
	) > colored_bed_%%x.json

)

# A Bork In Time

Java game made for fun!

## Ideas and To-Do

* Win condition
	* Win if the player reaches a certain color of the level, for now winning will just print something on the screen
* Cannons/Static Turrets
	* Will not fire towards player, but in a set direction and pace
	* Will fire different projectiles from normal turrets
		* Larger
		* slower
		* deals more damage
		* does not despawn on contact with player
	    * Cannot be destroyed
* Bubble ability
	* Costs ~40 mana
	* Creates a bubble shield around the player
	* shield will absorb one hit, regardless of the amount of damage
	* Health bar will have a semi transparent golden cover on it while the bubble is active
	* cannot stack more than one bubble
* EMP ability
	 * costs ~100 mana
	* disables all enemies on screen/within a certain range
  
## Level Design Guide

* Each pixel represents one tile
* the color and alpha of a pixel determine what will be spawned there
	* Pink: Player Spawn 0xffff00ff
	* White: Air 0xff000000
	* Black: Collision 0xff000000
	* Green: Turret 0xff00ff00
	* Red: Health Ball 0xffff0000
	* Alpha 255 (0xffff0000) Gives 100 Health, Alpha 155 (0x9bff0000) Gives 0 Health
	 * Anything below 155 will subtract health
	* Blue: Mana Ball 0xff0000ff
		* Alpha 255 (0xffff0000) Gives 100 mana, Alpha 155 (0x9bff0000) Gives 0 mana
		* Anything below 155 will subtract mana
	* Yellow: Lava 0xffffff00
	* Brown: Platform 0xff963200
	* Dark Yellow/Green: LargeEnemy 0xff646400
	* Other: Air 
		
* Turrets will automatically attach to the nearest surface
	* if there are multiple surfaces, the turret will prioritize below, right, left, then above
* Anything out of the map by default has normal collision, but outlining the level in black is still a good idea

package consts;

import java.util.HashMap;

public class Hero {

	public static final byte SOLDIER = 1;
	public static final byte TANK = 2;
	public static final byte ASSASSIN = 3;
	public static final byte GRABBER = 4;
	public static final byte MEDIC = 5;

	public static final byte SOLDIER_BOT = 11;
	public static final byte TANK_BOT = 12;
	public static final byte ASSASSIN_BOT = 13;
	public static final byte GRABBER_BOT = 14;
	public static final byte MEDIC_BOT = 15;

	public static final int SOLDIER_WIDTH = 40;
	public static final int SOLDIER_HEIGHT = 40;
	public static final int SOLDIER_SPEED = 250;
	public static final float SOLDIER_SHOT_FREQ = 0.2f;
	public static final int SOLDIER_AIM_BOT_COOLDOWN = 10;
	public static final int SOLDIER_AIM_BOT_SHOT_SPEED = 2200;
	public static final float SOLDIER_AIM_BOT_TIME = 1.5f;
	public static final int SOLDIER_SHOT_DMG = 40;
	public static final int SOLDIER_SHOT_SPEED = 1500;
	public static final int SOLDIER_MAX_HEALTH = 200;

	public static final int TANK_WIDTH = 60;
	public static final int TANK_HEIGHT = 60;
	public static final int TANK_SPEED = 150;
	public static final float TANK_SHOT_FREQ = 1f;
	public static final int TANK_MAX_HEALTH = 500;
	public static final int TANK_SHIELD_WIDTH = 75;
	public static final int TANK_SHIELD_DISTANCE = 65;
	public static final int TANK_SHATTER_DMG = 40;
	public static final int TANK_SHATTER_RANGE = 75;
	public static final int TANK_SHATTER_COOLDOWN = 6;
	public static final int TANK_SHATTER_SPEED = 400;
	public static final int TANK_SHATTER_COUNT = 12;
	public static final float TANK_SHATTER_SHIELD_AWAY = 0.75f;

	public static final int ASSASSIN_WIDTH = 30;
	public static final int ASSASSIN_HEIGHT = 30;
	public static final int ASSASSIN_SPEED = 225;
	public static final int ASSASSIN_SHOT_DMG = 32;
	public static final float ASSASSIN_SHOT_FREQ = 1f;
	public static final int ASSASSIN_SHOT_SPEED = 2000;
	public static final int ASSASSIN_DASH_DMG = 40;
	public static final int ASSASSIN_DASH_COOLDOWN = 4;
	public static final int ASSASSIN_DASH_CHARGES = 2;
	public static final int ASSASSIN_DASH_SPEED = 2000;
	public static final int ASSASSIN_MAX_HEALTH = 150;
	public static final float ASSASSIN_SHOT_LENGTH = 0.1f;
	public static final int ASSASSIN_SHOT_COUNT = 4;

	public static final int GRABBER_WIDTH = 55;
	public static final int GRABBER_HEIGHT = 55;
	public static final int GRABBER_SPEED = 150;
	public static final float GRABBER_SHOT_FREQ = 0.8f;
	public static final int GRABBER_SHOT_DMG = 18;
	public static final int GRABBER_SHOT_SPEED = 1000;
	public static final int GRABBER_SHOT_ANGLE = 30;
	public static final int GRABBER_SHOT_COUNT = 10;
	public static final int GRABBER_SHOT_RANGE = 150;
	public static final int GRABBER_MAX_HEALTH = 600;
	public static final int GRABBER_HOOK_SPEED = 1250;
	public static final int GRABBER_HOOK_RANGE = 375;
	public static final int GRABBER_HOOK_COOLDOWN = 5;
	public static final int GRABBER_HOOK_SHORT_RANGE = 75;
	public static final float GRABBER_HOOK_STUN = 0.75f;

	public static final int MEDIC_WIDTH = 38;
	public static final int MEDIC_HEIGHT = 38;
	public static final int MEDIC_SPEED = 150;
	public static final int MEDIC_SHOT_DMG = 22;
	public static final float MEDIC_SHOT_FREQ = 1f;
	public static final int MEDIC_SHOT_SPEED = 500;
	public static final int MEDIC_MAX_HEALTH = 200;
	public static final float MEDIC_SHOT_LENGTH = 0.1f;
	public static final int MEDIC_SHOT_COUNT = 3;
	public static final float MEDIC_AURA_COOLDOWN = 1f;
	public static final int MEDIC_AURA_RANGE = 100;
	public static final int MEDIC_AURA_HEAL_PER_SECOND = 50;
	public static final int MEDIC_AURA_SPEED_BOOST = 60;
	public static final int MEDIC_AURA_SPEED = 1;
	public static final int MEDIC_AURA_HEAL = 2;

	public static final HashMap<Byte, String> names = new HashMap<>();

	public static String getHeroName(byte heroType) {
		if (names.isEmpty()) {
			names.put(SOLDIER, "Soldier");
			names.put(TANK, "Tank");
			names.put(ASSASSIN, "Assassin");
			names.put(GRABBER, "Grabber");
			names.put(MEDIC, "Medic");
			names.put(MEDIC_BOT, "Bot");
			names.put(SOLDIER_BOT, "Bot");
			names.put(GRABBER_BOT, "Bot");
			names.put(TANK_BOT, "Bot");
			names.put(ASSASSIN_BOT, "Bot");
		}
		String name = names.get(heroType);
		return (name == null) ? "" : name;
	}

}

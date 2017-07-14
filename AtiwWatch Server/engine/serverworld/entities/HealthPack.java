package serverworld.entities;

import protocol.worlddata.HealthPackData;
import serverworld.ServerPlayer;

public class HealthPack extends HealthPackData{

	public static final float COOLDOWN = 6f;

	public HealthPack(int x, int y) {
		this.x = x;
		this.y = y;
		this.cooldownLeft = 0;
	}
	
	public HealthPack(HealthPackData data) {
		this.x = data.x;
		this.y = data.y;
		this.cooldownLeft = data.cooldownLeft;
	}

	public void update(float delta) {
		cooldownLeft -= delta;
	}

	public void use(ServerPlayer player) {
		if(cooldownLeft<=0 && player.health < player.maxHealth){
			cooldownLeft = COOLDOWN;
			player.fullLife();
		}
	}

	public float getCooldownLeft() {
		return Math.max(cooldownLeft, 0);
	}

	public HealthPackData getData(){
		HealthPackData data = new HealthPackData();
		data.x = x;
		data.y = y;
		data.cooldownLeft = cooldownLeft;
		return data;
	}
	
}

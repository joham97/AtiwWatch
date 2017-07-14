package serverworld;

import protocol.worlddata.PointData;
import server.GameServer;

public class ServerPoint {

	public byte team;
	public float capture;
	public boolean contested;

	public float team1Percent;
	public float team2Percent;

	public float timeTillCapturable;

	private byte winnerTeam;

	public ServerPoint() {
		team = 0;
		capture = 0;
		team1Percent = 0;
		team2Percent = 0;
		timeTillCapturable = 29.9999f;
		contested = false;
	}

	public void update(float delta, boolean team1, boolean team2, byte teamOnlyOnPoint) {
		contested = team1 && team2;
		timeTillCapturable -= delta;
		if (timeTillCapturable <= 0) {
			if (!contested) {
				if (teamOnlyOnPoint == 1) {
					capture -= delta;
				} else if (teamOnlyOnPoint == 2) {
					capture += delta;
				} else if (capture != 0) {
					capture += (capture < 0) ? delta / 2 : -delta / 2;
					if (Math.abs(capture) < 0.01) {
						capture = 0;
					}
				}
				if (capture < -1) {
					capture = -1;
					team = 1;
				} else if (capture > 1) {
					capture = 1;
					team = 2;
				}
			}
			if (team == 1) {
				team1Percent += delta / 120f;
				if (team1Percent >= 1f) {
					if (team2) {
						team1Percent = 119f / 120f;
					} else {
						winnerTeam = 1;
						GameServer.restart = true;
					}
				}
			} else if (team == 2) {
				team2Percent += delta / 120f;
				if (team2Percent >= 1f) {
					if (team1) {
						team2Percent = 119f / 120f;
					} else {
						winnerTeam = 2;
						GameServer.restart = true;
					}
				}
			}
		}
	}

	public PointData getData() {
		PointData pointData = new PointData();
		pointData.capturePercent = capture;
		pointData.contested = contested;
		pointData.pointTeam = team;
		pointData.team1Percent = team1Percent;
		pointData.team2Percent = team2Percent;
		pointData.winnerTeam = winnerTeam;
		pointData.timeTillCapturable = timeTillCapturable;
		return pointData;
	}

}

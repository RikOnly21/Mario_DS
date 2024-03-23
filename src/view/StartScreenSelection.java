package view;

public enum StartScreenSelection {
	START_GAME(0), VIEW_HELP(1), VIEW_ABOUT(2), GAME_SCORE(3);

	private final int lineNumber;

	StartScreenSelection(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public StartScreenSelection getSelection(int number) {
		if (number == 0)
			return START_GAME;
		else if (number == 1)
			return VIEW_HELP;
		else if (number == 2)
			return VIEW_ABOUT;
		else if (number == 3)
			return GAME_SCORE;
		else
			return null;
	}

	public StartScreenSelection select(boolean toUp) {
		int selection;

		if (lineNumber > -1 && lineNumber < 4) {
			selection = lineNumber - (toUp ? 1 : -1);

			if (selection == -1) selection = 3;
			else if (selection == 4) selection = 0;
			return getSelection(selection);
		}

		return null;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}

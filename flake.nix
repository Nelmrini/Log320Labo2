{
	description = "TicTacToe";

	inputs = {
		nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
	};

	outputs = { self, nixpkgs, ... }: let
		system = "x86_64-linux";
		pkgs = import nixpkgs { inherit system; };
	in {
		devShells."${system}".default = pkgs.mkShell {
			packages = with pkgs; [
				gradle
				jdk21
				zsh
			];
			shellHook = ''
				exec zsh
				if [[ $TMUX ]]; then
					tmux rename-window "nvim"
					tmux rename-session "TicTacToe"
				fi
			'';
		};
	};
}

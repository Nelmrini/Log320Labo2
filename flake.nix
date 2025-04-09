{
	description = "A giant tic tac toe solving program";

	inputs = {
		flake-parts.url = "github:hercules-ci/flake-parts";
		nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
	};

	outputs = inputs@{flake-parts, ... }: let
			project_name = "TicTacToe";
		in flake-parts.lib.mkFlake {inherit inputs;} {
			imports = [
				# flake module to import
			];
			systems = ["x86_64-linux"];
			perSystem = {config, pkgs, system, ... }: {
				devShells.default = pkgs.mkShell {
					packages = with pkgs; [
						zsh
						tmux
						gradle
					];
					shellHook = ''
						export SHELL=zsh
						SESSION_NAME="${project_name}"
						DEV_DIR=$PWD

						# Check if session exists
						if tmux has-session -t "$SESSION_NAME" > /dev/null 2>&1; then
							echo "Tmux $SESSION_NAME session already running."
							tmux switch-client -t "$SESSION_NAME":1
							exit 0
						fi

						# Check if session exists but is detached
						if tmux list-sessions | grep -q "$SESSION_NAME"; then
							echo "Tmux $SESSION_NAME session exists, attaching..."
							tmux attach-session -t "$SESSION_NAME":1
							exit 0
						fi

						tmux new-session -d -c "~" -s $SESSION_NAME

						tmux respawn-window -k -t $SESSION_NAME:1 -c $DEV_DIR "nix develop .#editor"
						tmux rename-window -t $SESSION_NAME:1 "editor"

						tmux new-window -t $SESSION_NAME:2 -n "git" -c $DEV_DIR "lazygit"
						tmux split-window -t $SESSION_NAME:2 -c $DEV_DIR

						tmux switch -t $SESSION_NAME:1

						exit 0;
					'';
				};
				devShells.editor = pkgs.mkShell {
					packages = with pkgs; [ gradle zsh ];
					shellHook = ''
						export PROJECT=TicTacToe
						export SHELL=zsh
						exec neovide
					'';
				};
			};
			flake = {};
		};
}

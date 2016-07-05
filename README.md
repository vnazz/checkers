
<p>    
	This project is a checkers game where the user plays against a minimax based ai. The code includes a board class
that handles the logic of the game, an aiMove class that chooses a move based on all possible moves for the next
three turns, a Move class that keeps track of where a piece originally was and where it is moving, a Tree class
that is used to organize all possible moves for the ai, and a game class that renders the board.
</p>

<p>
	The board class handles the bulk of game.  It keeps track of the state of the game, what moves are legal for
the current player, updates the board according to the move made, and responds to the users mouse movements. The
underlying mechanism of the board is an 2D array of color. Color is an enum in the board class.  The array knows
the location of all the pieces and which locations are empty.
</p>

<p>
	The aiMove class chooses a move by making a tree of height four.  The first level of the tree is the current
board. The next level is all the legal moves the ai can make.  The third level is all possible moves the user can
make based on the ai's moves and the fourth level is all the possible moves the ai can make after the
user makes its move.  Once this tree is made, it is scored by the number of pieces the ai has minus the number of
pieces the user has.  All kings have a weight of 3 while regular pieces have a weight of one.  The move is then
chosen by picking the maximum score of the leaves of the tree and replacing the scores of their parents on the third
layer. Now that the third level has the score of its children, the minimum value for each child on the third level is
chosen and replaces the score of the parents in the second layer.  The final score that represents the move that will
be made is the board with the maximum score in the second level. The chosen move has the ai making the best possible move
assuming that the player will make the best possible move on his / her turn.
</p>

<p>
	The Move class keeps track of the current location of the chosen piece and the location the piece will be moved.  It
also has a method that calculates the space in between the two locations which is used when trying to calculate if a jump
was made.
</p>

<p>
	The Tree class allows for as many children as desired. It is recursively defined as each child is a tree. Each tree
contains a board, a move, a score, and children.  These features were added so all the information that was needed to make
an aiMove was readily available.
</p>

<p>
	The way this game works is the user is always red and has the first move.  To make a move click on a piece and drag it
to the desired location and drop it.  If it is a valid move then the piece will appear there, if not try again. Once the user
has made his / her move then click and the ai will make its move.  The game will tell you once someone has won.  Also, double
jumps are required in this game.
</p>

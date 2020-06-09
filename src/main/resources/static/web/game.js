let shipJsonData = [{
		type: 'patrolboat',
		location: []
	},
	{
		type: 'submarine',
		location: []
	},
	{
		type: 'destroyer',
		location: []
	},
	{
		type: 'battleship',
		location: []
	},
	{
		type: 'carrier',
		location: []
	}
];

let salvosJsonData = {
	location: []
};

function getQueryVariable(variable) {
	let query = window.location.search.substring(1);
	let vars = query.split('&');
	for (let i = 0; i < vars.length; i++) {
		let pair = vars[i].split('=');
		if (pair[0] == variable) {
			return pair[1];
		}
	}
	return false;
}

const fetchGameView = fetch('/api/game_view/' + getQueryVariable('gp'));
const fetchGames = fetch('/api/games');
Promise.all([fetchGames, fetchGameView])
	.then(values => {
		return Promise.all(values.map(res => res.json()))
	})
	.then(([gameApi, gameViewApi]) => {
		console.log(gameViewApi);
		console.log(gameApi);
		battleShip();
		getShipLocation(gameViewApi);
		selectSalvoLocation();
		salvoLocation();
		showScrollingText(gameViewApi)
		paintSalvoes(gameViewApi.salvoes);
		appendBattleLog(gameViewApi);
		if (gameViewApi.battlelog) {
			paintHit(gameViewApi.battlelog.actions.opponentHits, gameViewApi.battlelog.actions.hits)
		};
		hideLogin(gameApi.player)
		appendFleetState(gameViewApi)
		shipStatusList(gameViewApi.battlelog.fleet)
	});


function showScrollingText(array) {
	let scrollingStatus = document.getElementById('scrollingStatus')
	let gameState = array.state;
	console.log(gameState);
	scrollingStatus.innerHTML += gameState
}

function getShipLocation(array) {
	if (array.ships.length != 0) {
		array.ships.forEach((ship) =>
			ship.location.map((el) => {
				document.getElementById(el).classList.add(ship.type);
			})
		);
		document.getElementById('roster-sidebar').style.visibility = 'hidden';
	} else if (array.ships.length == 0) {
		document.getElementById('roster-sidebar').style.visibility = 'visible';
	}
}

function hideLogin(player) {
	let loginBtn = document.getElementById('login-btn');
	let userIcon = document.createElement('h5');
	let navBar = document.getElementById('navbar-btn');
	let logoutBtn = document.getElementById('logoutBtn');
	if (player) {
		let playerName = player.name;
		// hide the elements
		loginBtn.classList.add('invisible');
		// userInfo 
		userIcon.setAttribute('class', 'p-2 bd-highlight');
		userIcon.innerHTML = playerName;
		navBar.appendChild(userIcon);
		logoutBtn.classList.remove('invisible');
	}
}

function selectSalvoLocation() {
	let cells = document.querySelectorAll('.grid-opponent > .grid-cell');
	cells.forEach((el) => {
		el.addEventListener('click', (e) => {
			if (el.classList == 'grid-cell') {
				el.classList.replace('grid-cell', 'salvoSelection');
				el.setAttribute('data-fired', 'true');
			} else {
				el.classList.replace('salvoSelection', 'grid-cell');
			}
		});
	});
}

function paintHit(arrayOpponentHit, arrayPlayerHit) {
	let opponentGrid = document.getElementById('opponent-grid')
	arrayOpponentHit.map((el) => {
		document.getElementById(el).classList.add("hits")
	});
	arrayPlayerHit.forEach((e) => {
		opponentGrid.querySelector('#' + e).classList.add('hits-opponent')
	})
}

function paintSalvoes(salvoArray) {
	parentOpponentGrid = document.getElementById('opponent-grid').childNodes;
	salvoArray.forEach((el) => {
		el.location.map((gridId) => {
			let salvoLocationId = gridId;
			for (let i = 0; i < parentOpponentGrid.length; i++) {
				if (salvoLocationId == parentOpponentGrid[i].id) {
					parentOpponentGrid[i].innerHTML = el.turn;
					parentOpponentGrid[i].classList.replace('grid-cell', 'fired');
				}
			}
		});
	});
}

function appendBattleLog(array) {
	let battleLogTable = document.getElementById('battlelog')
	let appendBattleLog = '';
	let battleLogContainer = document.getElementById('battleLogContainer')
	if (array.battlelog) {
		let hitsArray = array.battlelog.actions.hits;
		array.salvoes.forEach((el) => {
			battleLogContainer.classList.remove('invisible');
			let locationArray = el.location
			let hitsInSalvos = locationArray.filter(hits => hitsArray.includes(hits));
			appendBattleLog += '<tr><th scope="row">' + el.turn + '</th><td>' + el.location +
				'</td><td>' + hitsInSalvos + '</td></tr>'
		})
		battleLogTable.innerHTML += appendBattleLog
	}
}

function appendFleetState(array) {
	let fleetState = document.getElementById('fleet-state')
	if (array.ships.length != 0 && array.gamePlayers.length == 2) {
		console.log(array.ships);
		document.querySelector('#fleet-state').style.visibility = 'visible';
		fleetState.classList.remove('display-none');
	}
}

function logout() {
	fetch("/api/logout", {
		method: "POST",
		credentials: 'include'
	}).then((response) => {
		location.replace('/web/games.html')
	})
};


function salvoLocation() {
	salvosJsonData.location = [];
	let shotCells = document.querySelectorAll("[data-fired='true']");
	for (let i = 0; i < shotCells.length; i++) {
		let shotLocationId = shotCells[i].id;
		salvosJsonData.location.push(...[shotLocationId]);
	}
}

function shipStatusList(array) {
	for (let i = 0; i < array.length; index++) {
		let shipType = array[i].shipType
		if (array[i].isSunked == true) {
			const refs = document.querySelector("[data-shipListType='" + shipType + "']");
			console.log(refs);
			refs.innerHTML = 'Sunk!';
			refs.classList.add('shipSunkText')
		}
	}
}


function battleShip() {
	let ships = document.querySelectorAll('#fleet-roster div');
	let draggedItem = null;
	for (let i = 0; i < ships.length; i++) {
		const ship = ships[i];
		// rotate button
		document.getElementById('rotate-button').addEventListener('click', () => {
			if (ship.dataset.shipdirection === 'horizontal') {
				document.getElementById(ship.id).setAttribute('data-shipdirection', 'verticle');
			} else document.getElementById(ship.id).setAttribute('data-shipdirection', 'horizontal');
		});

		ship.addEventListener('dragstart', (e) => {
			draggedItemOne = e.dataTransfer.setData('text/plain', event.target.id);
			draggedItem = ship;
			// placeShip(ship);
			setTimeout(() => {
				draggedItem.classList.add('drag-action');
			}, 0);
		});
		document.getElementById('rotate-button').addEventListener('click', () => {
			if (ship.dataset.shiprotated === 'false') {
				console.log('hola');
				ship.dataset.shiprotated.replaceWith('true');
			} else if (ship.dataset.shiprotated === 'true') {
				ship.dataset.shiprotated.replaceWith('false');
			}
		});
		ship.addEventListener('dragend', () => {
			setTimeout(() => {
				draggedItem.style.display = 'block';
				draggedItem = null;
			}, 0);
		});
	}

	let gridCells = document.querySelectorAll('div.grid-cell');
	for (let i = 0; i < gridCells.length; i++) {
		const cells = gridCells[i];

		cells.addEventListener(
			'dragenter',
			function (event) {
				setTimeout(() => {
					cells.classList.add('drag-action');
				}, false);
			},
			100
		);
		cells.addEventListener('dragover', function (event) {
			event.preventDefault();
		});

		cells.addEventListener(
			'dragleave',
			function (event) {
				event.target.style.backgroundColor = '';
			},
			false
		);

		cells.addEventListener('drop', (event) => {
			const id = event.dataTransfer.getData('text');
			draggedItem = document.getElementById(id);
			cells.appendChild(draggedItem);
			placeShip(draggedItem);
		});
	}
}

function placeShip(ship) {
	// get parent element id
	let grid = ship.parentElement.id;
	let gridAlphabet = grid.slice(0, 1);
	let gridNum = grid.slice(1);

	shipJsonData[Number(ship.dataset.index)].location = [];
	for (let i = 0; i < ship.dataset.size; i++) {
		if (ship.dataset.shipdirection === 'horizontal') {
			let shipLocationIds = document.getElementById(gridAlphabet + (Number(gridNum) + i));
			if (shipLocationIds == null) {
				window.alert('Place ship inside the grid!');
				location.reload()

			}
			shipLocationIds.classList.add('add-grid-color');
			shipLocationIds.setAttribute('data-ship', ship.id);
			shipJsonData[Number(ship.dataset.index)].location.push(...[shipLocationIds.id]);
			shipName = shipLocationIds.dataset.ship;
			ship.addEventListener('dragstart', () => {
				shipLocationIds.classList.remove('add-grid-color');
				shipLocationIds.removeAttribute('data-ship');
			});
		} else if (ship.dataset.shipdirection === 'verticle') {
			let shipVerticleIds = document.getElementById(
				numToSSColumn(lettersToNumber(gridAlphabet) + i) + Number(gridNum)
			);
			shipVerticleIds.classList.add('add-grid-color');
			shipJsonData[Number(ship.dataset.index)].location.push(...[shipVerticleIds.id]);
			shipName = shipVerticleIds.dataset.ship;
			ship.addEventListener('dragstart', () => {
				shipVerticleIds.classList.remove('add-grid-color');
				shipVerticleIds.removeAttribute('data-ship');
			});
		}
	}

	function numToSSColumn(num) {
		var s = '',
			t;
		while (num > 0) {
			t = (num - 1) % 26;
			s = String.fromCharCode(65 + t) + s;
			num = ((num - t) / 26) | 0;
		}
		return s || undefined;
	}

	function lettersToNumber(gridAlphabet) {
		for (var p = 0, n = 0; p < gridAlphabet.length; p++) {
			n = gridAlphabet[p].charCodeAt() - 64 + n * 26;
		}
		return n;
	}
}

function saveShips(shipJsonData) {
	fetch('/api/games/players/' + getQueryVariable('gp') + '/ships', {
			credentials: 'include',
			headers: {
				Accept: 'application/json',
				'Content-Type': 'application/json'
			},
			method: 'POST',
			body: JSON.stringify(shipJsonData)
		})
		.then(function (res) {
			console.log(res);
			if (res.ok == true) {
				console.log('hola');
				setTimeout(function () {
					location.reload(); // then reload 
				}, 1000);
			}
			return res.json();
		})
		.then((data) => {
			console.log(data);
		})
		.catch(function (res) {
			console.log(res);
		});
}

function saveSalvos(salvosJsonData) {
	salvosJsonData.location = [];
	let shotCells = document.querySelectorAll("[data-fired='true']");
	for (let i = 0; i < shotCells.length; i++) {
		let shotLocationId = shotCells[i].id;
		salvosJsonData.location.push(...[shotLocationId]);
	}
	fetch('/api/games/players/' + getQueryVariable('gp') + '/salvoes', {
			credentials: 'include',
			headers: {
				Accept: 'application/json',
				'Content-Type': 'application/json'
			},
			method: 'POST',
			body: JSON.stringify(salvosJsonData)
		})
		.then(function (res) {
			console.log(res);
			if (res.ok == true) {
				console.log('hola');
				setTimeout(function () {
					location.reload(); // then reload 
				}, 500);
			}
			return res.text();
		})
		.then((data) => {
			console.log(data)
			window.alert(data)
		})
		.catch((error) => {});
}
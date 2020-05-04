function getQueryVariable(variable) {
    let query = window.location.search.substring(1);
    let vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        let pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return (false);
}

fetch("/api/game_view/" + getQueryVariable("gp"), {
    method: "GET",
}).then(function (res) {
    return res.json();
}).then(
    (gameViewApi) => {
        data = gameViewApi
        console.log(data)
        //showPlayers(gameViewApi)
        // getShipLocation(data)
        // paintSalvoLocation(data)
        // dragDropShips()
        battleShip()
        // createGrids()
    }
)

function showPlayers(array) {
    let toAppend = '';
    array.gamePlayers.forEach(el => {
        console.log(el.player);
    });
    document.getElementById("container").innerHTML += toAppend;
}

function getShipLocation(array) {
    array.ships.map(ship =>
        ship.location.map(loc => document.getElementById(loc).style.backgroundColor = "red"));
}

function paintSalvoLocation(array) {
    appendX = 'X'
    array.salvoes.map(salvo => salvo.location.map(sal => document.getElementById(sal).innerHTML += appendX))
}

function battleShip() {
    let ships = document.querySelectorAll(".fleet-roster li")
    console.log(ships);
    let draggedItem = null;
    for (let i = 0; i < ships.length; i++) {
        const ship = ships[i]

        ship.addEventListener('dragstart', (e) => {
            draggedItemOne = e.dataTransfer.setData('text/plain', event.target.id)
            draggedItem = ship;
            setTimeout(() => {
                ship.style.color = 'grey';
            }, 0)
        });

        ship.addEventListener('dragend', () => {
            setTimeout(() => {
                draggedItem.style.display = 'block';
                draggedItem = null;
            }, 0)
        })
    }

    let gridCells = document.querySelectorAll('div.grid-cell')
    for (let i = 0; i < gridCells.length; i++) {
        const cells = gridCells[i]

        cells.addEventListener("click", function (event) {
            console.log(event.target.id);
        })

        cells.addEventListener("dragenter", function (event) {
                setTimeout(() => {
                    event.target.style.backgroundColor = "grey"
                }, false);
            },
            100)
        cells.addEventListener("dragover", function (event) {
            event.preventDefault();
        });

        cells.addEventListener("dragleave", function (event) {
            event.target.style.backgroundColor = '';
        }, false);

        cells.addEventListener("drop", (event) => {
            const id = event.dataTransfer.getData('text');
            console.log(id);

            cells.appendChild(draggedItem);
            draggedItem = document.getElementById(id)
            event.target.style.backgroundColor = 'grey';
            console.log(cells.id);

        })
    }


}
/*
event
    .dataTransfer
    .setData('text/plain', event.target.id);
*/


/*
classes = classes.replace('placing', '');
fleetList[i].setAttribute('class', classes)


function dragDropShips() {
    let gridList = document.querySelectorAll('.grid-item')
    let shipList = document.querySelectorAll('.ships')
    // let shipContainer = document.getElementById("shipsSubContainer")
    let draggedItem = null;

    for (let i = 0; i < shipList.length; i++) {
        var ship = shipList[i];
        console.log(i);

        ship.addEventListener('dragstart', (e) => {
            console.log('dragStart', e);
            draggedItem = ship;
            console.log(draggedItem);
            setTimeout(() => {
                ship.style.display = 'none';
            }, 0)
        });

        ship.addEventListener('dragend', () => {
            setTimeout(() => {
                draggedItem.style.display = 'block';
            }, 0)
        });

        for (let j = 0; j < gridList.length; j++) {
            const grid = gridList[j];
            grid.addEventListener('dragover', (e) => {
                e.preventDefault();
            });

            grid.addEventListener('dragenter', (e) => {
                e.preventDefault();
                grid.style.backgroundColor = 'rgba(0, 0, 0, 0.2)';
            });

            grid.addEventListener('dragleave', function (e) {
                grid.style.backgroundColor = 'rgba(0, 0, 0, 0.1)';
            });

            grid.addEventListener('drop', (e) => {
                grid.append(draggedItem)
            })
        }
    }
}
function createGrids() {
    let newDiv = document.createElement("div");
    //Generate the alphabets with number 
    for (i = 1; i < 11; i++) {
        let gridAlphabets = (((9 + i).toString(36).toUpperCase()));
        console.log(gridAlphabets);
        newDiv.id = '"' + gridAlphabets + '"';
        console.log(newDiv.id);

        newDiv.className = "grid-item"
    }

}
*/
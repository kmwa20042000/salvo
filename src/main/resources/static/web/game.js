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
        showPlayers(gameViewApi)
        getShipLocation(data)
        paintSalvoLocation(data)
    }
)

function showPlayers(array) {
    let toAppend = '';
    for (let i = 0; i < array.gamePlayers.length; i++) {
        toAppend += '<h2>' + array.gamePlayers[0].player.player.email + '(you)' + '</h2><h2>vs</h2><h2>' + array.gamePlayers[1].player.player.email + '</h2>'
    }
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
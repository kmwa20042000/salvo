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

const fetchGames = fetch('/api/games');
const fetchScores = fetch('/api/score_board');

Promise.all([fetchGames, fetchScores])
    .then(values => {
        return Promise.all(values.map(res => res.json()))
    })
    .then(([gameApi, scores]) => {
        console.log(scores);
        console.log(gameApi);
        playerApi = gameApi.player;
        createGameListOne(gameApi)
        createScoreBoard(scores)
        document.getElementById("loginBtn").addEventListener("click", function () {
            actLogin()
        });
        document.getElementById("regBtn").addEventListener("click", function () {
            actRegister()
        });

        document.getElementById("createGameBtn").addEventListener("click", function () {
            createGame()
        })
        hideLogin(gameApi.player)
    });

function createScoreBoard(object) {
    let appendScoreBoard = ''
    let data = [];
    let keys = [];
    for (let key in object) {
        if (object.hasOwnProperty(key)) {
            keys.push(key);
            data.push(object[key])
        }
        data.sort((a, b) => b.total - a.total)
    }
    data.forEach(el => {
        appendScoreBoard += '<tr><td>' + el.userName + '</td><td>' + el.total + '</td><td>' + el.w + '</td><td>' + el.l + '</td><td>' + el.t + '</td></tr>'
    })
    document.getElementById("scoreBoard").innerHTML += appendScoreBoard
}

function hideLogin(player) {
    let loginFrom = document.getElementById('login-from');
    let loginBtn = document.getElementById('login-btn');
    let userIcon = document.createElement('h5');
    let navBar = document.getElementById('navbar-btn');
    let logoutBtn = document.getElementById('logoutBtn');
    let createGameBtn = document.getElementById('createGameBtn')
    if (player) {
        let playerName = player.name;
        // hide the elements
        loginFrom.classList.add('invisible');
        loginBtn.classList.add('invisible');
        // userInfo 
        userIcon.setAttribute('class', 'p-2 bd-highlight');
        userIcon.innerHTML = playerName;
        navBar.appendChild(userIcon);
        logoutBtn.classList.remove('invisible');
        createGameBtn.classList.remove('invisible');
    }
}


function getPlayer(array) {
    let toAppend = '';
    for (let i = 0; i < array.gamePlayer.length, i++;) {
        toAppend += '<dt>' + array.gamePlayer[i].email + '</dt>'
    }
    console.log(toAppend);
}

function logout() {
    fetch("/api/logout", {
        method: "POST",
        credentials: 'include'
    }).then((response) => {
        location.reload()
    })
};

function rejoinGame(array, gameArray, player) {
    let button = "Must be looged in!";
    if (player) {
        if (array.length == 2) {
            if ((array[0].player.id != player.id && array[1].player.id != player.id)) {
                button = '    </td>'
            } else {
                button = '<a href=/web/game.html?gp=' + array[0].gpid + ' class="btn btn-dark" id="joinGameBtn">Rejoin game</td>'
            }
        } else {
            if (array[0].player.id == player.id) {
                button = '<a href=/web/game.html?gp=' + array[0].gpid + ' class="btn btn-dark" id="joinGameBtn">Rejoin game</td>'
            } else {
                button = '<div id="rejoinBtn" class="btn btn-dark" data-game=' + gameArray.gameId + ' onclick="joinGame()">Join game</div></td>'
            }
        }
    }
    return button;
}

function createGameListOne(array) {
    let appendTo = '';
    for (let i = 0; i < array.games.length; i++) {
        let gameArray = array.games[i];
        appendTo += '<tr><td>' + gameArray.gameId + '</td><td>' + gameArray.date + '</td><td>' + gameArray.gamePlayer[0].player.name + '</td><td>' + (gameArray.gamePlayer.length == 2 ? array.games[i].gamePlayer[1].player.name : 'Waiting for opponent') + '</td><td>' + rejoinGame(gameArray.gamePlayer, array.games[i], array.player) + '</tr>'
    }
    document.getElementById("gameList").innerHTML += appendTo;
}

/*
function logout(evt) {
    $.post("/api/logout")
        .done()
        .fail()
}
*/

function actLogin() {
    let form = document.forms.loginForm;
    let userNameInput = form.elements.username;
    let userPassword = form.elements.password;
    fetch("/api/login", {
            credentials: "include",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            method: "POST",
            body: getBody({
                username: userNameInput.value,
                password: userPassword.value
            })
        })
        .then(function (res) {
            console.log(res);
            if (res.ok == true) {
                setTimeout(function () {
                    location.reload();
                }, 500);
            } else {
                window.alert("Incorrect Credentials!")
            }
            return res.json();
        }).then((data) => {
            console.log(data);
        })
        .catch(function (error) {
            // window.alert("Request failure: player doesn't exists ", error);
        });

    //prepare the data in the way how springboot secutity want it.
    function getBody(json) {
        var body = [];
        for (var key in json) {
            var encKey = encodeURIComponent(key);
            var encVal = encodeURIComponent(json[key]);
            body.push(encKey + "=" + encVal);
        }
        return body.join("&");
    }
}

$('.message a').click(function () {
    $('form').animate({
        height: "toggle",
        opacity: "toggle"
    }, "slow");
});



function actRegister() {
    let regForm = document.forms.regForm;
    let userFirstNameInput = regForm.elements.firstName;
    let userLastNameInput = regForm.elements.lastName;
    let userPasswordInput = regForm.elements.password;
    let userEmailInput = regForm.elements.userName;

    fetch("/api/player", {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify({
                "firstName": userFirstNameInput.value,
                "lastName": userLastNameInput.value,
                "password": userPasswordInput.value,
                "userName": userEmailInput.value
            })
        })
        .then(function (res) {
            console.log(res)
            window.alert("registered! Please login now with the credential")
            location.reload()
        })
        .catch(function (res) {
            console.log(res)
        })
}

function createGame() {
    fetch("/api/games", {
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "POST",
        })
        .then(function (res) {
            console.log(res)
            return res.json();
            //location.reload(true)
        })
        .then(data => {
            window.location.replace('/web/game.html?gp=' + data.gpId)
            console.log(data);
        })
        .catch(function (res) {
            console.log(res)
        })
}

function joinGame() {
    const joinGameBtn = document.getElementById('rejoinBtn');
    const gameId = joinGameBtn.dataset.game
    console.log(gameId);
    fetch(' /api/game/' + gameId + '/players', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "POST",
        })
        .then(function (res) {
            console.log(res)
            return res.json();
        })
        .then(data => {
            console.log(data.gpId);
            window.location.replace('/web/game.html?gp=' + data.gpId)
        })
        .catch(function (res) {
            console.log(res)
        })
}
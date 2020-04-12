let data = '';
fetch("/api/games", {
    method: "GET",
}).then(function (res) {
    return res.json();
}).then(
    (gameApi) => {
        this.data = gameApi;
        console.log(gameApi);
        createGameListOne(gameApi);
        getScore(gameApi);
        // loginBtn();
        document.getElementById("loginBtn").addEventListener("click", function () {
            actLogin()
        });

    })

function getPlayer(array) {
    let toAppend = '';
    for (let i = 0; i < array.gamePlayer.length, i++;) {
        toAppend += '<dt>' + array.gamePlayer[i].email + '</dt>'
    }
    console.log(toAppend);

}

function createGameListOne(array) {
    let appendTo = '';
    array.forEach(el => {
        el.gamePlayer.forEach(gamePlayer => {
            appendTo += '<tr><td>' + el.gameId + '</td><td>' + el.date + '</td><td>' + gamePlayer.player.name + '</td></tr>'
        })
    });
    document.getElementById("gameList").innerHTML += appendTo;
}

function getScore(array) {
    let tableRow = [];
    let result = [];
    let apend = '';
    array.forEach(array => {
        array.score.forEach(scoreArray => {
            result.push(scoreArray)
        })
    });
    console.log(result);
    result.forEach(sc => {
        let gameBoardCol = {
            playerEmail: "",
            totalScore: 0,
            wins: 0,
            losts: 0,
            ties: 0,
            totalPoints: 0,
        };
        gameBoardCol.playerEmail = sc.player
        console.log(gameBoardCol.playerEmail);

        //use to sort the list later, after figure out how to combine the duplicated players.
        tableRow.push(gameBoardCol)

        if (sc.score == "1") {
            gameBoardCol.wins++
            gameBoardCol.totalPoints++
        } else if (sc.score == "0") {
            gameBoardCol.losts++
        } else if (sc.score == "0.5") {
            gameBoardCol.ties++
            gameBoardCol.totalPoints = gameBoardCol.totalPoints + 0.5
        }
        console.log(gameBoardCol.ties);
        apend += '<tr><td>' + gameBoardCol.playerEmail + '</td><td>' + gameBoardCol.totalPoints + '</td><td>' + gameBoardCol.wins + '</td><td>' + gameBoardCol.losts + '</td><td>' + gameBoardCol.ties + '</td></tr>'
    })
    document.getElementById("scoreBoard").innerHTML += apend;
}

function loginBtn() {
    document.getElementById("loginBtn").addEventListener("click", function login(evt) {
        evt.preventDefault();
        var form = evt.target.form;
        $.post("/api/login", {
                name: form["username"].value,
                pwd: form["password"].value
            })
            .done(function () {
                console.log("logged in!");
            })
            .fail()
    })
}

function logout(evt) {
    evt.preventDefault();
    $.post("/api/logout")
        .done()
        .fail()
}

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
        .then(function (data) {
            console.log(userPassword.value)
            console.log(data)
        })
        .catch(function (error) {
            console.log("Request failure: ", error);
        });

    //prepare the data in the way how springboot secutity want it.
    function getBody(json) {
        var body = [];
        for (var key in json) {
            var encKey = encodeURIComponent(key);
            var encVal = encodeURIComponent(json[key]);
            body.push(encKey + "=" + encVal);
            console.log(body);
            console.log(encKey);
            console.log(encVal);
        }
        return body.join("&");
    }
}
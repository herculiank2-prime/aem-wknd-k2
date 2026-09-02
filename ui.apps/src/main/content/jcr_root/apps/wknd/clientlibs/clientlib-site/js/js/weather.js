(function () {

    const card = document.querySelector(".weather-card");

    if (!card) {
        return;
    }

    const endpoint = card.dataset.weatherEndpoint;

    fetch(endpoint)
        .then(response => response.json())
        .then(data => {

            card.querySelector(".temperature")
                .textContent = data;
        })
        .catch(error => {

            console.error("Unable to load weather", error);

            card.querySelector(".temperature")
                .textContent = "--";
        });

})();

class Animator{
    constructor(config){
        ['simulationId', 'canvas', 'simulationEndpoint', 'instantEndpoint'].map(prop => {
            if(!config.hasOwnProperty(prop)){
                throw `Agregar ${prop} a los parámetros de configuración`;
            }
        });
        this.simulation = this.getSimulation(config.simulationEndpoint, config.simulationId);
        this.canvasId = config.canvas;
        this.cellSize = config.cellSize || 80;
        this.instantEndpoint = config.instantEndpoint;

        if(this.simulation == undefined){
            throw "simulación no encontrada";
        }

        this.loadResources();
    }

    loadResources(){
        this.grass = document.getElementById("grass_pic");
        this.tree = document.getElementById("tree_pic");
        this.fire = document.getElementById("fire_pic");
        this.droneImage = document.getElementById("drone_pic");
    }

    getSimulation(endpoint, simulationId){
        var simulation;
        sendToAPI(
            {
                url: endpoint,
                data: {simulationId: simulationId},
                type: 'GET',
                success: function(data){
                    simulation = JSON.parse(data);
                },
                error: function(message) {
                    swal("Error", message.responseText, "error");
                }
            });
        return simulation;
    }

    start(){
        var canvas = document.getElementById(this.canvasId);
        canvas.width = this.simulation.width * this.cellSize;
        canvas.height = this.simulation.height * this.cellSize;

        var con = canvas.getContext("2d");
        var animator = this;

        var t = 1;
        while (t < this.simulation.endingTime) {
            (function(t) {
                setTimeout(function() {
                    var instant = animator.findInstant(t);
                    animator.drawInstant(instant, con);
                    console.log(instant);
                }, 2000 * t)
            })(t++)
        }

    }

    drawInstant(instant, con){
        for(var y in Object.keys(instant.objects)){
            for(var x in Object.keys(instant.objects[y])){
                this.drawElement(con, instant.objects[y][x], x, this.simulation.height - y - 1);
                this.drawFire(con, instant.fires,x, y );
            }
        }

        this.drawDrones(con, instant.drones);
    }

    drawDrones(con, drones){
        for(var index in drones){
            var drone = drones[index];
            var canvasX = drone.x * con.canvas.width / this.simulation.height;
            var canvasy = con.canvas.height - drone.y * con.canvas.height / this.simulation.height;
            con.drawImage(this.droneImage, canvasX, canvasy, this.cellSize/2, this.cellSize/2);
            console.log(canvasX + " - "+canvasy);
        }
    }

    drawElement(con, element, x, y){
        var image;
        if(element == 'TREE'){
            image = this.tree;
        }else if(element == "GRASS"){
            image = this.grass;
        }else{
            image = this.grass;
            //todo: poner ground
        }
        con.drawImage(image, x*this.cellSize, y*this.cellSize, this.cellSize, this.cellSize);
    }

    drawFire(con, fires, x, y){
        if(fires[y] != undefined && fires[y][x] != undefined){
            var image = this.fire;
            con.drawImage(image, x*this.cellSize, this.cellSize*(this.simulation.height - y - 1), this.cellSize, this.cellSize);
        }
    }

    findInstant(t){
        var simulationId = this.simulation.id;
        var instant;
        sendToAPI(
            {
                url: this.instantEndpoint,
                data: {simulationId: simulationId, from: t, offset: 1},
                type: 'GET',
                success: function(data){
                    instant = JSON.parse(data)[0];
                },
                error: function(message) {
                    swal("Error", message.responseText, "error");
                }
            });
        return instant;
    }
}
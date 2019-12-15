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
        this.t = 1;
        this.bar = this.loadBar();
    }

    loadBar(){
        var animator = this;
        return new ProgressBar.Line(progressBar, {
            strokeWidth: 4,
            easing: 'easeInOut',
            duration: 1400,
            color: '#FFEA82',
            trailColor: '#eee',
            trailWidth: 1,
            svgStyle: {width: '100%', height: '100%'},
            text: {
                style: {
                    // Text color.
                    // Default: same as stroke color (options.color)
                    color: '#f1f1f1',
                    position: 'absolute',
                    right: '-60px',
                    top: '0px',
                    padding: 0,
                    margin: 0,
                    transform: null
                },
                autoStyleContainer: false
            },
            from: {color: '#FFEA82'},
            to: {color: '#ED6A5A'},
            step: (state, bar) => {
                bar.setText(animator.t+"/"+animator.simulation.endingTime);
            }
        });
    }

    drawBar(time){
        this.t = time;
        var progress = time/this.simulation.endingTime;
        this.bar.set(progress);
    }

    loadResources(){
        this.grass = document.getElementById("grass_pic");
        this.tree = document.getElementById("tree_pic");
        this.fire = document.getElementById("fire_pic");
        this.droneImage = document.getElementById("drone_pic");
        this.groundImage = document.getElementById("ground_pic");
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
        while (t <= animator.simulation.endingTime) {
            (function(t) {
                setTimeout(function() {
                    var instant = animator.findInstant(t);
                    animator.drawInstant(instant, con);
                    animator.drawBar(t);
                    console.log(instant);
                }, 500 * t)
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

        this.drawDrones(con, instant.droneGroups);
    }

    drawDrones(con, droneGroups){
        for(var index in droneGroups){
            var group = droneGroups[index];
            this.drawDroneGroup(con,group);
        }
    }

    drawDroneGroup(con, droneGroup){
        var y = this.simulation.height - droneGroup.y - 1
        var groupSize = droneGroup.list.length;
        var offset = (this.cellSize*.7)/groupSize;

        if(groupSize > 1){
            con.strokeText(groupSize,droneGroup.x*this.cellSize, y*this.cellSize);
        }

        for(var index in droneGroup.list){
            var drone = droneGroup.list[index];
            this.drawDrone(con,drone, droneGroup.x, y, index*offset);
        }
    }

    drawDrone(con, drone, x, y, offset){
        con.drawImage(this.droneImage,
            x*this.cellSize + offset,
            y*this.cellSize + offset,
            this.cellSize/2, this.cellSize/2);
    }

    drawElement(con, element, x, y){
        con.drawImage(this.groundImage, x*this.cellSize, y*this.cellSize, this.cellSize, this.cellSize);
        var image;
        if(element == 'TREE'){
            image = this.tree;
        }else if(element == "GRASS"){
            image = this.grass;
        }else{
            return;
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
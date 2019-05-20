#Proyecto Final:  

##Idea general
Uso del paradigma multiagentes para actuar sobre objetivos múltiples en forma cooperativa. 

Los agentes pueden aprender y transmitir el aprendizaje obtenido.

##Partes del sistema

- Agente: Entidad inteligente que buscar aplicar una o varias acciones sobre los objetivos que pueda alcanzar hasta poderlos llevar a un estado o valor determinado.

- Objetivo: Entidades estáticas o móviles, temporales o permanentes, de valor constante o variable.

- Escenario: 
Marca el tiempo del sistema. 
Contiene referencia parcial o total de los agentes y sus objetivos.

- Acción de un Agente: es lo que puede hacer un agente en un determinado momento para lograr su objetivo.

##Agente

###Comportamiento de un agente
A continuación se describen los tres comportamientos principales de un agente:


####Observación
Queda en la implementación decidir si el alcance de la visión será limitada o absoluta.

De ser limitada se definirá un radio de alcance.

####Aprendizaje
Los agentes en cada paso pueden aprender qué acción es más efectiva para un momento determinado. 

Hay dos métodos para lograr esto:
- Recibir el conocimiento de otro agente que ya pasó por la experiencia.
- Aprender de la experiencia por cuenta propia a través de aplicar alguna acción sobre el entorno o el objetivo.

####Transmisión de conocimiento
Una vez que el agente considera que aprendió algo util lo puede difundir en forma multicast o broadcast a otros agentes.

El conocimiento que se transmite esta asociado a una acción y sus parámetros.

La medida de "conocimiento util" será definido en cada implementación.

###Ciclo de vida de un agente

Por cada paso el agente primero hace una observación del escenario. Donde decide si actualiza su lista de agentes y objetivos.

Luego por cada acción que tiene programada decide si ejecutarla. 
Cada acción tiene asociada una lista de parámetros que son aplicados en el momento que el agente decide correrla. 
La aplicación de una acción puede tener efectos sobre el propio agente, sus vecinos y objetivos.
La accion, al ser aplicada, retorna un numero real que indica qué tan beneficiosa fue para la misión del agente. Este resultado puede ser aprendido.

Por último se puede compartir lo aprendido a los otros agentes. 

~~~java
public void step(){
    observe();
    for(Action action : actions){
        if(action.decide()){
            double result = action.apply();
            learn(result, action.getId());
        }
    }
    transmit();
}
~~~


##Diseño

###Agent.java 
Clase abstract que tiene referencia al World, un conjunto de Target y una lista de Action asociadas.

En cada implementación se debe programar:  

~~~java
    public abstract void learn(double result, int id);

    public abstract void observe();

    public abstract void transmit();
~~~

###Target.java
Clase o Interface que es objetivo de los Agent

###World.java
Contiene referencia parcial o total a los Target y Agent

###Action.java
Clase abstracta que tiene un id para que se puede identificar la tarea entre los distentos objetos.

También tiene una probabilidad de ocurrencia para que cada agente decida si la ejecuta o no.

El método apply devuelve un número que indica que tan buena fue la acción para lograr un mejor efecto sobre el efectivo.

![Diagrama de clase](src/main/resources/images/class_diagram.png)


##Casos de aplicación
Implementando algunas clases clave sobre la estructura base se puede aplicar el comportamiento descritpto en otros casos específicos.
###Lucha contra incendios

###Lucha contra la sequia de un campo

###Terapeutas

###Prevención de delitos con drones
package sparkserver

import spark.kotlin.Http
import spark.kotlin.ignite
import com.google.gson.Gson
import spark.*

data class Car(val id:Int, val model:String = "default model", var damaged:Boolean = false, val doors:Int = 0, val country:String = "default country")

var cars = mutableListOf<Car>()
var idx = 1
fun main() {
    val http: Http = ignite()
    with(http) {
        port(getHerokuPort())
        staticFiles.location("/public")
        get("/add") { add(request, response) }
        get("/text") { text(request, response) }
        get("/json") { json(request, response) }
        get("/html") { html(request, response) }
        get("/deleteall") { delAll(request, response) }
        get("/delete/:id") { del(request, response) }
        get("/update/:id") { update(request, response)}
    }
}

fun getHerokuPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        processBuilder.environment()["PORT"]!!.toInt()
    } else 5000
}

fun add(req: Request, res: Response): String {
    val id = idx++

    var model = req.queryParams("model")
    if(model==null) model = "default model"

    var damaged = false
    if(req.queryParams("damaged")!=null) damaged = req.queryParams("damaged") == "on"

    var doors = 0
    if(req.queryParams("doors")!=null) doors = req.queryParams("doors").toInt()

    var country = req.queryParams("country")
    if(country==null) country = "default model"

    cars.add(Car(id, model, damaged, doors, country))
    return "car added to list, size = $id"
}
fun text(req: Request, res: Response): String {
    return cars.toString()
}
fun json(req: Request, res: Response): String {
    return Gson().toJson(cars)
}
fun html(req: Request, res: Response): String {
    var htm = "<style>table{border: 1px solid black;}table *{border: 1px solid black;}</style>"
    htm+="<table>"
    for(i in (0 until cars.size)){
        htm+="<tr>"
        htm+="<td>"+cars[i].id+"</td><td></td>"
        htm+="<td>"+cars[i].model+"</td>"
        htm+="<td>"+cars[i].damaged+"</td>"
        htm+="<td>"+cars[i].doors+"</td>"
        htm+="<td>"+cars[i].country+"</td>"
        htm+="</tr>"
    }
    htm+="</table>"
    return htm
}
fun delAll(req: Request, res: Response): String {
    cars = mutableListOf<Car>()
    return "lista jest pusta"
}
fun del(req: Request, res: Response): String {
    var id = 0
    for (i in 0 until cars.size) {
        if (cars[i].id == req.params("id").toInt()) id = i
    }
    cars.removeAt(id)
    return "usuniete: ${req.params("id")}"
}
fun update(req: Request, res: Response): String {
    var id = 0
    for (i in 0 until cars.size) {
        if (cars[i].id == req.params("id").toInt()) id = i
    }
    cars[id].damaged = !cars[id].damaged
    return "zaktualizowane: ${req.params("id")} na ${cars[id]}"
}

1	Clean Architecture   ✅

2	Inyección de dependencias con Hilt/Dagger	❌

3	BindingAdapters	✅

4	Extensiones	✅

5	Base de datos Room ❌

6	Consulta de WS mediante Retrofit ✅

7	Convertidores de objetos entre capas (Entitiy a Model y Model a Entity) ✅

8	Casos de uso (Use Cases) ✅

9	Repositorios ✅

10	LiveData ✅

11	DataBinding	✅

12	Corutinas	✅

13	Pruebas Unitarias con MockK	✅

14	Control de versiones en GitHub ✅


⚠️ Nota sobre Hilt y Room

No se utilizó Hilt ni Room debido a problemas de compatibilidad entre versiones de Kotlin, Room y Hilt, específicamente al utilizar Kotlin 2.0.21 y los procesadores de anotaciones con kapt. 

❌ Problemas encontrados:

  Hilt genera errores en kapt y dagger.hilt.android.plugin.task.AggregateDepsTask al compilar con Kotlin 2.0.21.
  
  Room (versión 2.8.0-beta01 y anteriores) presenta errores al procesar anotaciones con ksp o kapt en Kotlin 2.x
  
  La integración de Hilt y Room genera conflictos cuando se usa con versiones recientes de Compose y Google Maps SDK.

✅ Soluciones adoptadas:

  Se utilizó inyección manual de dependencias mediante ViewModelFactory.
  
  Se reemplazó Room por SharedPreferences + Gson, suficiente para almacenar y recuperar datos simples localmente.

package taxipark

import sun.security.krb5.internal.crypto.crc32
import kotlin.math.max
import kotlin.math.roundToLong

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        this.allDrivers.filter { it !in this.trips.associateBy {trip -> trip.driver }}.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        allPassengers.associate{it to trips.count{trip -> trip.passengers.contains(it) }}.filter { it.value >= minTrips }.keys.toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        allPassengers.associate {
            Pair(it, driver) to trips.count{trip -> trip.passengers.contains(it) && driver == trip.driver }
        }.filter {it.value > 1 }.keys.toMap().keys

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> =
        allPassengers.associate {
            Pair(it, trips.count{trip -> it in trip.passengers }) to trips.count{trip -> it in trip.passengers && trip.discount != null }}
                .filter { it.value > it.key.second/2}.keys.toMap().keys

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? =
    if(!trips.isEmpty()) trips.groupBy {(it.duration/10)*10..(it.duration/10)*10+9}.entries.sortedByDescending { it.value.size }.get(0).key
    else null


/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    val twentyPercent = allDrivers.size*20/100

    val totalIncome = trips.sumByDouble { it.cost }

    val eightyPercentIncome = totalIncome*0.8

    val tripsByDriver = trips.groupBy { it.driver }

    var totalIncomeByDriver= mutableMapOf<Driver, Double>()

    for (entry in tripsByDriver){
        totalIncomeByDriver.put(entry.key,entry.value.sumByDouble { trip -> trip.cost })
    }

    val sortedIncomeByDriver = totalIncomeByDriver.entries.sortedByDescending { it.value}

    var sumDriverPercent = 0.0;

    for (i in 0 until twentyPercent){
        if (!totalIncomeByDriver.isEmpty())sumDriverPercent += sortedIncomeByDriver.get(i).value
    }

    return if(sumDriverPercent >= eightyPercentIncome && !totalIncomeByDriver.isEmpty()) true else false
}
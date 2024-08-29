# transit-system

An object-oriented transit system. It can configure station maps, store customer information, calculate fares based on the location and time that a user tapped in and tapped out on a station, and make logs. The project received a near-perfect score due to its fault tolerance.

The main java file is at transit-system/src/application/Main.java

Made by: Hantang Li(https://github.com/Hantang-Li), Liang Kaiqu (https://github.com/kevinliang888), Hongyu Ao(https://github.com/aohongyu), Yifan Bai

## Getting Started
You may find some “Cannot resolve” symbols when checking the code because you haven’t
imported the external libraries we used. The path of external libraries is: /transitSystem/libs

This is a simple guide to show you how to import them to IntelliJ IDEA
1. Open IntelliJ IDEA, click File -> Project Structure
2. Click Modules -> Dependencies -> small “+” button -> JARS or directories…
3. Select two .jar files in libs (path: /transitSystem/libs), then click Open/OK.
4. Check two external libraries, then click OK.

## Prerequisites
This program only works with JDK 1.8

## Design

Observer:
1. It is used in WeeklyPass to observe the system time that is modified in adminUser since they are
not related to avoiding coupling and making sure each WeeklyPass can get the current system
time immediately.
2. It is used in the MVC design pattern. Firstly, whenever the user taps in or out, the status label
will be modified by observing the changes in transitManager. Secondly, if the user edit his user name
successfully, the label(view), which represents the user name will be modified by observing the
changes in accountManager.

Iterator:
To delete data at a specified date while iterating.

Strategy:
The system requires different strategies while deducting money.

Serialize:
To save all the information of this system(stations, trips, revenues) and customers (cards and accounts)

Factory:
When creating different kinds of cards that inherit from TransitPass.

Logging:
To save all the system information that involved instance change(INFO LEVEL) and Exception
raised(WARING or SEVERE), and some operations(FINE) for debugging.

Singleton:
Since the inquiries require this system can generate only one log file, "log.txt", we compared some
implementations. It will result in all the classes calling AdminUser often if we define a static log-in AdminUser. Implementing different log instances in different managers will cause Java to generate
different log files, ex: "log.txt.1", "log.txt.2", etc. Therefore, we decided to use Singleton to
implement the log.

• This design also follows all of the five SOLID principles.

1. Single responsibility principle
All the operations involved in content change on this system are separated by three Managers.
For report-generating functions and view-information functions, we can use each specified
instance's function, such as isOwingMoney and toString in transitPass, and view information functions
are all done by admin.

2. Open closed principle
This system is open since it is still available for further extension, all functions are specified
and separately placed in each class. You are welcome to add any new features by simply adding new
class or strategy or adding if statements in the top module. This system is closed since we don't need to
modify some base functions while adding new features.

3. Liskov substitution principle
We followed this principle while defining cards class: all cards under TransitPass can be replaced
by each other, and we also have AbleTopUp interface. All cards under this interface are free to
substitute by each other.

4. Interface segregation principle
Since some cards need topUp function while some are not, we created an AbleTopUp interface for those
cards. Since there are different types of stations: "subway station" and "bus stop", and the fare
calculation is different, so we use the FareStrategy interface to calculate fares for different types of
stations.


5. Dependency inversion principle
When we implementing TransitPass superclass, we avoid implementing top up function and all cards
under this class are only able to record information and deduct, while all other methods are for
visiting information, but not for change content. (also applied single responsibility principle here)

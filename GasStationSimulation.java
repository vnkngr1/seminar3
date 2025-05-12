import java.util.*;

// Класс автомобиля, который приезжает на заправку
class Car {
    int id;
    int tankSize;
    int fuelLevel;
    int desiredFuel;
    int arrivalTime; // Время прибытия в виртуальном времени

    public Car(int id, int tankSize, int fuelLevel, int desiredFuel, int arrivalTime) {
        this.id = id;
        this.tankSize = tankSize;
        this.fuelLevel = fuelLevel;
        this.desiredFuel = Math.min(desiredFuel, tankSize - fuelLevel);
        this.arrivalTime = arrivalTime; // Фиксация времени прибытия в эмуляции
    }
}

// Класс топливной колонки, обслуживающей автомобили
class FuelPump {
    int id;
    int speed;     // Скорость заправки (литров в минуту)
    int busyUntil; // Время, до которого колонка занята

    public FuelPump(int id, int speed) {
        this.id = id;
        this.speed = speed;
        this.busyUntil = 0; // Изначально колонка свободна
    }
}

// Класс, представляющий заправочную станцию
class GasStation {
    Queue<Car> waitingQueue = new LinkedList<>(); // Очередь машин, ожидающих свободную колонку
    List<FuelPump> pumps = new ArrayList<>();     // Список колонок на заправке
    int fuelSupply;                               // Общее количество топлива на станции
    int maxQueueTime = 12;                        // Максимальное время ожидания в очереди в минутах
    int currentTime = 0;                          // Виртуальное время в минутах
    int refillThreshold = 500;                    // Порог, при котором заказывается топливо
    int refillAmount = 5000;                      // Объем пополнения топлива

    public GasStation(int initialPumps, int initialFuel) {
        for (int i = 1; i <= initialPumps; i++) {   // Номера колонок начинаются с 1
            pumps.add(new FuelPump(i, 20));  // Инициализация колонок со скоростью 20 литров в минуту
        }
        this.fuelSupply = initialFuel; // Запас топлива
    }

    private String formatTime(int time) {
        int hours = (time / 60) % 24;
        int minutes = time % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    // Метод обработки автомобилей в текущий момент времени
    public void processCars() {
        for (FuelPump pump : pumps) {
            if (pump.busyUntil <= currentTime && !waitingQueue.isEmpty()) { // Если колонка свободна
                Car car = waitingQueue.poll();
                if (fuelSupply >= car.desiredFuel) {
                    fuelSupply -= car.desiredFuel;
                    pump.busyUntil = currentTime + car.desiredFuel / pump.speed; // Рассчитываем, когда колонка освободится
                    System.out.println("[" + formatTime(currentTime) + "] Автомобиль " + car.id + " начал заправку на колонке " + pump.id);
                } else {
                    System.out.println("[" + formatTime(currentTime) + "] Недостаточно топлива для автомобиля " + car.id);
                    refillFuel(); // Запрос на пополнение топлива
                }
            }
        }
    }

    // Метод добавления нового автомобиля
    public void arriveCar(Car car) {
        System.out.println("[" + formatTime(currentTime) + "] Автомобиль " + car.id + " прибыл на заправку.");
        waitingQueue.add(car);
        System.out.print("[" + formatTime(currentTime) + "] На данный момент автомобилей в очереди " + waitingQueue.size());
    }

    // Метод проверки очереди
    public void checkQueue() {
        for (Car car : waitingQueue) {
            if (currentTime - car.arrivalTime > maxQueueTime) {
                System.out.println("[" + formatTime(currentTime) + "] Открывается новая колонка!");
                pumps.add(new FuelPump(pumps.size() + 1, 20)); // Добавляем новую колонку с правильной нумерацией
                break;
            }
        }
    }

    // Метод пополнения запаса топлива, если его мало
    public void refillFuel() {
        if (fuelSupply < refillThreshold) {
            fuelSupply += refillAmount;
            System.out.println("[" + formatTime(currentTime) + "] Запас топлива пополнен на " + refillAmount + " литров. Теперь в наличии: " + fuelSupply);
        }
    }
}

public class GasStationSimulation {
    static Random random = new Random(); // Генератор случайных чисел для появления машин
    static int carCounter = 0;           // Счётчик автомобилей

    public static void main(String[] args) {
        GasStation station = new GasStation(3, 5000); // Создаем заправку с 3 колонками и запасом 5000 литров топлива

        for (int time = 0; time < 1440; time++) { // Эмуляция 24 часов (1440 минут)
            station.currentTime = time;

            int carSpawnCount = random.nextInt(7); // Генерация от 0 до 6 машин
            for (int i = 0; i < carSpawnCount; i++) {
                Car car = new Car(++carCounter, random.nextInt(40) + 500, random.nextInt(30), random.nextInt(200) + 10, time);
                station.arriveCar(car); // Добавляем машину на заправку
            }
            station.processCars(); // Запускаем процесс заправки
            station.checkQueue();  // Проверяем, не пора ли открыть новую колонку
        }
    }

}

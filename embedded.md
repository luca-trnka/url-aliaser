# On-demand air pressure measurement

## Before you start coding
The exercise has to make sure about the followings:

- Compiles
- Does not have any undefined behaviour
- Does not crash
- Does not leak memory
- Does not use any blocking mechanism (like HAL_Delay() or HAL_UART_Receive())

## Summary of operation and goals
As a part of a big project your task is to create an embedded software, which
can measure air pressure in a smart watch application. The air pressure will be
measured with an analog sensor connected to the MCU. When the user pushes a
button on the smart watch the air pressure is measured and stored together
with a timestamp in the MCU memory.

The final hardware is currently not available, but you can use an
STM32F746G-DISCO development board and a potentiometer to simulate the
air pressure change. The blue pushbutton can be also used to simulate the user
button press. CMSIS-OS over freeRTOS embedded real-time operating system
must be used in your solution.

Your task is to:
- detect a pushbutton event using an EXTI interrupt and signal the event to an
RTOS task using a signal,

- in the RTOS task start an ADC conversion and store the air pressure and the
timestamp in a vector (see vector specification later),

- in an other RTOS task print out the vector content to the USB-UART interface
in every 1000ms (see formatting specifications later).

## Vector implementation

Each measurement should be stored in a simple structure which is part of the
specification:

```c
struct air_pressure {
  float pressure_kPa;
  uint32_t timestamp_ms;
} air_pressure_t;
```

To store the measurement data you must use a custom vector data structure, 
which has the following features:
- dynamically allocated

  - starts with 0 capacity
  
  - when the vector is full then twice as much memory is allocated as before
  (except the very first allocation where the capacity is 0. The first allocation
  should make sure that the vector starts on 10 capacity)

- represented by an appropriete `struct`

- implements (same functionality as in C++ `std::vector`)
  - `void push_back` receives a vector and and an `air_pressure_t` and puts it at the end of the vector
  - `void pop_back` receives a vector and removes the last element of the vector
  - `air_pressure_t at` receives a vector and an index and returns the air_pressure_t structure stored on the given index 
  - `bool empty` receives a vector and returns a bool whether it's empty or not
  - `int size` recives a vector and returns its size
  - `int capacity` receives a vector and returns its capacity

**Important Note**: Your functions will be used in FreeRTOS task conext
so you should use `pvPortMalloc` and `vPortFree` instead of `malloc` and `free`
for dynamic memory allocation and deallocation.

## User button detection
Configure the user button as EXTI interrupt source and
implement the interrupt handler. You can generate code with STM32CubeIDE CubeMX
tool.

## Analog measurement
The potentiometer output should be connected to the A1 pin and the
ADCx peripheral should be configured to be able to measure the voltage on
pin A1 with blocking HAL_ADC functions (interrupt or DMA based measruement
is not required).

The relation between the measured voltage and the pressure can be considered
linear and defined by the following table:

| Voltage in mV | Air pressure in kPa |
| ------------- | ------------------- |
| 0             | 90                  |
| 1650          | 105                 |
| 3300          | 120                 |

## Putting things together
You must implement the application with the following RTOS tasks:

- `void air_pressure_meas(void const *param)`
- `void air_pressure_print(void const *param)`

As the vector is accessed from both task **you must protect it from concurrent
access** with a mutex.

The serial output should look like the following after a few seconds and 
5 pushbutton events:

```gen
----------------------
Vector is empty
----------------------
Vector is empty
----------------------
1.    0008178ms 109kPa
2.    0008816ms 101kPa
----------------------
1.    0008178ms 109kPa
2.    0008816ms 101kPa
3.    0009910ms 090kPa
----------------------
1.    0008178ms 109kPa
2.    0008816ms 101kPa
3.    0009910ms 090kPa
4.    0010820ms 117kPa
----------------------
1.    0008178ms 109kPa
2.    0008816ms 101kPa
3.    0009910ms 090kPa
4.    0010820ms 117kPa
5.    0011850ms 090kPa
```
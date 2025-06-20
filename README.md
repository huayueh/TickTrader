# TickTrader

## Project Overview

TickTrader is a Java-based application designed for backtesting trading strategies using historical tick data. It provides a framework for developing and testing strategies for futures and options trading.

## How it Works

TickTrader processes historical tick data and allows you to test custom trading strategies. The core components of the system include:

*   **`TickService`**: Responsible for reading and processing tick data from specified data sources. It then feeds this data to the active trading strategy.
*   **`Strategy`**: This is where the trading logic is implemented. You can create custom strategies by extending the base `Strategy` interface or abstract classes. Strategies receive tick data from the `TickService` and can generate trading signals or actions.
*   **`Recorder`**: Used to log various types of data during backtesting, such as ticks, trades, and strategy performance. This helps in analyzing the results of a backtest.
*   **`ContractProvider`**: Defines the financial instruments (contracts) that will be used in the backtest. It provides details about the contracts, such as their symbols and types.

The typical data flow is as follows:
1.  Historical tick data is read by the `TickService`.
2.  The `TickService` passes the ticks to the configured `Strategy`.
3.  The `Strategy` processes the ticks, applies its logic, and can use a `Recorder` to log relevant information.

## Input Data

To run a backtest with TickTrader, you will need to provide the following:

*   **Historical Tick Data**: This is the primary input for the system. Tick data should be provided in files, typically in a CSV-like format, where each row represents a single price tick. The exact format might depend on how you implement or configure the data reading part of the `TickService`. The examples provided in the `example` module read data from local directories.
*   **Contract Configurations**: You need to define the contracts you want to trade. This includes information like the contract symbol (e.g., "TX" for
    Taiwan Stock Exchange Futures), type (e.g., Future, Option), and any other relevant parameters required by your strategies or contract providers.
*   **Trading Strategies**: These are Java classes that implement your trading logic. You will need to create these based on the `Strategy` interface.

## Getting Started

1.  **Build the Project**:
    TickTrader uses Maven as its build system. To build the project, navigate to the root directory of the project in your terminal and run:
    ```bash
    mvn clean package
    ```
    This command will compile the source code, run any tests (if not skipped), and package the application into JAR files, which will be located in the `target` directory of each module.

2.  **Run an Example**:
    The `example` module contains several example classes that demonstrate how to run a backtest. For instance, you can run the `FutureDayTradeExample`.
    To do this, you would typically execute the compiled JAR or run the main method from your IDE.

    **Important**:
    *   The example classes often have hardcoded paths to tick data (e.g., `/Users/harvey/Downloads/Tick/future/` in `FutureDayTradeExample.java`). You **must** modify these paths to point to the location of your historical tick data.
    *   You will need to prepare your tick data in a format that the example's `TickService` (e.g., `FutureTickService`) can understand.

    Here's a snippet from `FutureDayTradeExample.java` showing where you might need to make changes:

    ```java
    // In FutureDayTradeExample.java
    public static void main(String arg[]) throws URISyntaxException {
        int year = 2021; // Configure the year for the data
        // Ensure the recorder path is valid
        Strategy strategy = new FutureDayTradeStrategy(new FileTickRecorder(Paths.get("2021-12-02.csv")), new FixContractProvider("202112"));
        // IMPORTANT: Update this path to your tick data directory
        TickService tickService = new FutureTickService("/PATH/TO/YOUR/TICK/DATA/future/", year, strategy);
        // Define the contract to trade
        tickService.addContract(new Contract("TX", Contract.ANY, Contract.ANY_PRICE, FutureType.FUTURE));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
    ```

3.  **Develop Your Strategy**:
    To test your own trading ideas, you'll need to:
    *   Create a new Java class that implements the `Strategy` interface (or extends an existing abstract strategy).
    *   Implement your trading logic within this class.
    *   Modify one of the example main methods (or create a new one) to use your custom strategy.

## Modules

The TickTrader project is organized into the following Maven modules:

*   **`core`**: This module contains the essential classes and interfaces for the TickTrader application. This includes the `TickService`, `Strategy`, `Recorder`, data transfer objects (`dto`), and various providers.
*   **`example`**: This module provides example implementations and configurations to demonstrate how to use the `core` module to run backtests. It includes sample strategies and main classes to execute different types of backtests (e.g., for futures or options).

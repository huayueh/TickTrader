## TickTrader Project

### What is this project for?
This project, TickTrader, is designed to process financial tick data for futures and options. It reads tick data from files, transforms it into a structured format, and allows for the application of custom trading strategies. The core functionality includes parsing different tick data formats for futures and options, handling contract details, and recording processed data.

### How it works
The project is structured into a `core` module and an `example` module.
- The `core` module contains the main logic for:
    - Reading tick data from CSV-like files.
    - Parsing lines of data into `Tick` objects, which represent individual price movements. Different services (`FutureTickService`, `OptionTickService`) handle specific formats.
    - Normalizing symbols and contract information.
    - Providing a framework for implementing and applying trading `Strategy` interfaces.
    - Recording processed ticks and strategy outputs.
- The `example` module demonstrates how to use the `core` module.

The system typically processes data by:
1. Configuring a `TickService` (e.g., `FutureTickService`) with a base folder containing data files and a specific year.
2. The service reads data files line by line.
3. Each line is parsed into a `Tick` object by the `wrapTick` method of the configured service.
4. A `Strategy` object, passed to the service, can then process these `Tick` objects.

### Expected input data
The project expects input data in CSV-like text files. The format varies slightly between futures and options:

**Futures Data:**
Each line should be a comma-separated string with the following fields:
1.  `Date`: (e.g., `20230115`)
2.  `Symbol`: (e.g., `FIMTX`)
3.  `Contract`: (e.g., `202303`)
4.  `Time`: (e.g., `090000` or `090000123` - only the first 6 digits are used for HHmmss)
5.  `Price`: (e.g., `15000.50`)
6.  `Quantity`: (e.g., `10`)

*Example Future Line:*
`20230115,FIMTX,202303,090000123,15000.50,10`

**Options Data:**
Each line should be a comma-separated string with the following fields:
1.  `Date`: (e.g., `20230115`)
2.  `Symbol`: (e.g., `TXO`)
3.  `Exercise Price`: (e.g., `15000`)
4.  `Contract`: (e.g., `202303`)
5.  `Put/Call`: (`P` for Put, `C` for Call)
6.  `Time`: (e.g., `090000` or `090000123` - only the first 6 digits are used for HHmmss)
7.  `Price`: (e.g., `120.75`)
8.  `Quantity`: (e.g., `5`)

*Example Option Line:*
`20230115,TXO,15000,202303,C,090000234,120.75,5`

The system reads files based on a base directory and year specified when a service is initialized. It expects data files to be present in a structure that the service can locate (typically within year-specific subdirectories or matching certain patterns, though this is largely determined by how the `AbstractTickService`'s file reading methods are implemented or overridden).

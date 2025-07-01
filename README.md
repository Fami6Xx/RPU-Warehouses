# RPU-Warehouses

A Minecraft plugin that adds warehouse functionality to the RPUniverse plugin.

## Features

- Create warehouses for specific jobs
- Store and retrieve items from warehouses
- Permission-based access control
- Simple sign-based interface

## How to Use

### Creating a Warehouse

1. Place a sign
2. On the first line, write `[Warehouse]`
3. On the second line, write the job name
4. The sign will be formatted automatically

### Using a Warehouse

- **Right-click** a warehouse sign to view its contents
- **Shift + right-click** with an item to add it to the warehouse
- **Shift + left-click** to take the first item from the warehouse

## Permissions

- `rpuwarehouses.create` - Allows creating warehouses
- `rpuwarehouses.use` - Allows viewing warehouse contents
- `rpuwarehouses.add` - Allows adding items to warehouses
- `rpuwarehouses.take` - Allows taking items from warehouses

## Implementation Details

### Data Model

- `Warehouse` - Represents a warehouse for a specific job
  - Stores items in a map with material names as keys
  - Supports adding and removing items
  - Serializable for persistence

### Management

- `WarehouseManager` - Manages warehouses
  - Loads and saves warehouses to disk
  - Provides methods to create, retrieve, and remove warehouses
  - Handles adding and removing items from warehouses

### User Interface

- Sign-based interface for creating and interacting with warehouses
- Text-based display of warehouse contents
- Simple commands for adding and taking items

### Localization

- Language strings for all messages and UI elements
- Support for placeholders in messages

## Future Improvements

- Add a GUI menu for selecting items from warehouses
- Add a GUI menu for selecting the amount of items to take
- Add commands for managing warehouses
- Add support for item metadata (enchantments, custom names, etc.)
- Add support for multiple warehouses per job
- Add statistics tracking for warehouse usage
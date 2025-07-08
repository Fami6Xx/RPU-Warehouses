# RPU-Warehouses

A Minecraft plugin that adds warehouse functionality to the RPUniverse plugin. It allows players to create and manage warehouses for specific jobs, storing and retrieving items through a simple sign-based interface.

## Features

- Create warehouses for specific jobs
- Store and retrieve items from warehouses
- Permission-based access control
- Simple sign-based interface
- GUI menu for viewing warehouse contents
- Page limit system for controlling warehouse storage capacity
- Support for item metadata (display names, lore, enchantments)
- Multiple warehouses per job

## Installation

1. Make sure you have [RPUniverse](https://github.com/Fami6Xx/RP-Universe) v1.5.0 or later installed
2. Download the latest version of RPU-Warehouses
3. Place the JAR file in your server's `plugins` folder
4. Restart your server

## How to Use

### Creating a Warehouse

1. Place a sign
2. On the first line, write `[Warehouse]`
3. On the second line, write the job name
4. The sign will be formatted automatically

### Using a Warehouse

- **Right-click** a warehouse sign to view its contents in a GUI menu
- **Shift + right-click** with an item to add it to the warehouse
- **Shift + left-click** to take the first item from the warehouse
- **Shift + right-click** with a book to open the page limit menu (requires admin permission)

### Commands

- `/warehouse pagelimit <jobName> <pageLimit>` - Set the page limit for a job's warehouse (requires admin permission)

## Permissions

- `rpuwarehouses.create` - Allows creating warehouses (default: op)
- `rpuwarehouses.use` - Allows viewing warehouse contents (default: true)
- `rpuwarehouses.add` - Allows adding items to warehouses (default: true)
- `rpuwarehouses.take` - Allows taking items from warehouses (default: true)
- `rpuwarehouses.admin` - Allows administrative actions like setting page limits (default: op)

## Configuration

### Page Limits

Each job's warehouse has a page limit that controls how many unique items it can store. By default, each job has a page limit of 1, which allows storing up to 28 unique items (28 slots per page).

You can set a job's page limit in two ways:
1. Use the command: `/warehouse pagelimit <jobName> <pageLimit>`
2. Shift + right-click a warehouse sign with a book, then select the desired page limit in the GUI

Page limits are stored in `plugins/RPU-Warehouses/page_limits.json`.

### Warehouses

Warehouse data is stored in `plugins/RPU-Warehouses/warehouses.json`.

## Dependencies

- [RPUniverse](https://github.com/Fami6Xx/RP-Universe) v1.5.0 or later
- Paper/Spigot 1.14 or later

## Implementation Details

### Data Model

- `Warehouse` - Represents a warehouse for a specific job
  - Stores items in a map with unique keys based on item type and metadata
  - Supports adding and removing items
  - Serializable for persistence
  - Respects page limits for storage capacity

### Management

- `WarehouseManager` - Manages warehouses
  - Loads and saves warehouses to disk
  - Provides methods to create, retrieve, and remove warehouses
  - Handles adding and removing items from warehouses
  - Updates warehouse signs to reflect current state

- `JobPageLimits` - Manages page limits for each job's warehouse
  - Loads and saves page limits to disk
  - Provides methods to get and set page limits

### User Interface

- Sign-based interface for creating and interacting with warehouses
- GUI menus for viewing warehouse contents and setting page limits
- Command interface for administrative actions

### Localization

- Language strings for all messages and UI elements
- Support for placeholders in messages

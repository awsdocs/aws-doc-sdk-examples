package main

import (
	"log"

	"github.com/gofiber/fiber/v2"
)

func main() {
	app := fiber.New()

	// Return a string
	app.Get("/", func(c *fiber.Ctx) error {
		return c.SendString("Hello, World!")
	})

	// Return some JSON
	app.Get("/greet/:name", func(c *fiber.Ctx) error {
		return c.SendString("{'greeting':'Hello, " + c.Params("name") + "!'}")
	})

	log.Fatal(app.Listen(":3000"))
}

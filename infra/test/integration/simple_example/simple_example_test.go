package simple_example

import (
	"fmt"
	"testing"

	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/gcloud"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/tft"
	"github.com/stretchr/testify/assert"
)

func TestSimpleExample(t *testing.T) {
	const databaseVersion = `MYSQL_8_0`

	// initialize Terraform test from the Blueprints test framework
	example := tft.NewTFBlueprintTest(t)

	example.DefineVerify(func(assert *assert.Assertions) {

		dbIP := example.GetStringOutput("dp_ip")
		op := gcloud.Run(t, "sql instances describe")
		fmt.Print(dbIP)
		fmt.Print(op.String())
		assert.NotEmpty(dbIP, "db_ip")
	})

	example.Test()
}

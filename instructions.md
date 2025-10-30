Based on the work we've done to convert the Double Widget node to use the modern WebUI pattern, here's a comprehensive step-by-step guide that can be applied to convert other nodes:

## **Step-by-Step Guide: Converting Legacy Widget Nodes to Modern WebUI Pattern**

### **Prerequisites**
- Identify the target node (e.g., `SomeWidgetNodeFactory`)
- Locate the corresponding configuration node (e.g., `SomeDialogNodeFactory`) if it exists
- Check the existing XML file for node descriptions

### **Step 1: Create Shared Parameter Class**
Create a shared parameter class that both Configuration and Widget nodes will use:

**Location:** `/src/org/knime/js/base/node/parameters/{type}/`
**File:** `{Type}NodeParameters.java` (e.g., DoubleNodeParameters.java)

**Content includes:**
- All UI settings (default value, validation options, etc.)
- Proper `@Widget`, `@Layout`, and `@Section` annotations
- Custom persistors for backward compatibility using legacy config keys
- Overwritten value message handling
- Optional widgets for min/max values or other validation settings

### **Step 2: Update Configuration Node Settings**
Modify the existing configuration settings to use the shared parameters:

**File:** `{Type}DialogNodeSettings.java`
**Changes:**
```java
// Replace all individual parameter fields with:
@PersistWithin.PersistEmbedded
{Type}NodeParameters m_{type}NodeParameters = new {Type}NodeParameters();

// Remove all duplicated parameter definitions
```

### **Step 3: Create Widget Node Parameters**
Create widget-specific parameter wrapper:

**Location:** `/src/org/knime/js/base/node/widget/input/{type}/`
**File:** `{Type}WidgetNodeParameters.java`

**Content:**
```java
@SuppressWarnings("restriction")
public final class {Type}WidgetNodeParameters extends WidgetNodeParameters {

    {Type}WidgetNodeParameters() {
        super({Type}InputWidgetConfig.class);
    }

    @PersistWithin.PersistEmbedded
    {Type}NodeParameters m_{type}NodeParameters = new {Type}NodeParameters();
}

```

### **Step 4: Update Widget Node Factory**
Convert the legacy factory to use the modern pattern:

**File:** `{Type}WidgetNodeFactory.java`
**Changes:**

1. **Update inheritance:**
   ```java
   // FROM:
   extends NodeFactory<{Type}WidgetNodeModel> implements WizardNodeFactoryExtension<...>
   
   // TO:
   extends WidgetNodeFactory<{Type}WidgetNodeModel, {Type}NodeRepresentation<{Type}NodeValue>, {Type}NodeValue>
   ```

2. **Add WebUI configuration:**
   ```java
   static final String DESCRIPTION = "Creates a {type} input widget for use in components views. Outputs a {type} flow variable with a given value.";

   @SuppressWarnings({"deprecation", "restriction"})
   static final WebUINodeConfiguration CONFIG = WebUINodeConfiguration.builder()//
       .name("{Type} Widget") //
       .icon("./widget_{type}.png") //
       .shortDescription(DESCRIPTION) //
       .fullDescription(DESCRIPTION) //
       .modelSettingsClass({Type}WidgetNodeParameters.class) //
       .addOutputPort("Flow Variable Output", FlowVariablePortObject.TYPE,
           "Variable output ({type}) with the given variable defined.") //
       .nodeType(NodeType.Widget) //
       .keywords() //
       .build();
   ```
   Use the given skelton, but use the descriptions as specified in the {type}NodeFactory.xml

3. **Update constructor:**
   ```java
   @SuppressWarnings("javadoc")
   public {Type}WidgetNodeFactory() {
       super(CONFIG, {Type}WidgetNodeParameters.class);
   }
   ```

4. **Remove boilerplate methods:**
   - Remove `getNrNodeViews()`
   - Remove `createNodeView()`
   - Remove `hasDialog()`
   - Keep only `createNodeModel()` and `createNodeDialogPane()`

### **Step 5: Handle Persistence**
Create custom persistors for backward compatibility:

**In the shared parameter class, add persistors for:**
- Default values (if stored in special locations)
- Optional min/max values using `useMin`/`min` pattern
- Any other legacy settings that need special handling

**Example:**
```java
static final class MinValuePersistor extends ValidationValuePersistor {
    MinValuePersistor() {
        super({Type}NodeConfig.CFG_USE_MIN, {Type}NodeConfig.CFG_MIN);
    }
}
```

### **Step 6: Create Test Infrastructure**
Create snapshot tests for the new parameters:

1. **Add XML settings file:**
   `/org.knime.js.tests/files/node_settings/{Type}WidgetNodeParameters.xml`

2. **Create test class:**
   `/org.knime.js.tests/src/org/knime/js/base/node/widget/input/{type}/{Type}WidgetNodeParametersTest.java`
   
   ```java
   @SuppressWarnings("restriction")
   final class {Type}WidgetNodeParametersTest extends DefaultNodeSettingsSnapshotTest {
       
        protected {Type}WidgetNodeParametersTest() {
            super(CONFIG);
        }

        private static final SnapshotTestConfiguration CONFIG = SnapshotTestConfiguration.builder() //
            .testJsonFormsForModel({Type}WidgetNodeParameters.class) //
            .testJsonFormsWithInstance(SettingsType.MODEL, () -> readSettings()) //
            .testNodeSettingsStructure(() -> readSettings()) //
            .build();

        private static {Type}WidgetNodeParameters readSettings() {
            try {
                var path = getSnapshotPath({Type}WidgetNodeParametersTest.class).getParent().resolve("node_settings")
                    .resolve("{Type}WidgetNodeParameters.xml");
                try (var fis = new FileInputStream(path.toFile())) {
                    var nodeSettings = NodeSettings.loadFromXML(fis);
                    return NodeParametersUtil.loadSettings(nodeSettings.getNodeSettings(SettingsType.MODEL.getConfigKey()),
                        {Type}WidgetNodeParameters.class);
                }
            } catch (IOException | InvalidSettingsException e) {
                throw new IllegalStateException(e);
            }
        }

   }

   ```

### **Step 7: Verification Steps**
1. **Compile check:** Ensure no compilation errors
2. **Test XML structure:** Verify the XML can be loaded by the test
3. **Backward compatibility:** Ensure existing workflows still load correctly
4. **UI consistency:** Check that both Configuration and Widget nodes have identical UI

### **Key Patterns to Follow**

#### **Imports to Add:**
- `org.knime.core.webui.node.impl.WebUINodeConfiguration`
- `org.knime.js.base.node.widget.WidgetNodeFactory`
- `org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin`

#### **Imports to Remove:**
- `org.knime.core.node.wizard.WizardNodeFactoryExtension`
- Legacy NodeFactory inheritance patterns

#### **Annotations to Use:**
- `@Widget(title, description)` for form fields
- `@Layout(SectionClass.class)` for organization
- `@OptionalWidget` for optional fields
- `@Persistor(CustomPersistor.class)` for backward compatibility
- `@PersistWithin.PersistEmbedded` for parameter embedding

### **Common Pitfalls to Avoid**
1. Don't create separate persistors for Configuration vs Widget - use the same shared parameters
2. Don't forget to copy descriptions from the XML file
3. Don't remove the legacy dialog creation - it's still needed
4. Ensure proper section layouts match the original dialog structure
5. Test with existing workflow files to ensure backward compatibility

This pattern ensures both Configuration and Widget nodes share identical settings while maintaining full backward compatibility and providing a modern WebUI experience.

### Last steps

1. Create a git branch based on the given branch name (e.g. enh/UIEXT-3005-web-ui-migration-double-widget). The new branch should be based on the current branch
1. Commit the changes with the following commit message skeleton:
```UIEXT-3005: Migrate Double Widget to WebUI

UIEXT-3005 (WebUI-Migration Double Widget)```
That means, the number behind UIEXT must be adapted and the type (Double)
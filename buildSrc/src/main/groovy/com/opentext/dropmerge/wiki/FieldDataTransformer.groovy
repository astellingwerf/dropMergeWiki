package com.opentext.dropmerge.wiki

class FieldDataTransformer {

    public String transformFieldData(FormField field) {
        def methodName = "transform$field.name"
        def transformer = this.&"$methodName"
        def to = this.metaClass.respondsTo(this, methodName)
        if (transformer && to) {
            switch (transformer.maximumNumberOfParameters) {
                case 2: transformer = transformer.rcurry(field.content)
                case 1: transformer = transformer.rcurry(field.rawItem)
                case 0:
                    break;
                default:
                    throw new IllegalStateException("transform$field.name should have 0, 1, or 2 parameters")
            }
            String result = transformer.call()
            println "$field.name=$result"
            return result

        } else {
            return field.content
        }
    }
}

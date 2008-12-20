package org.hypergraphdb.viewer.painter;


public class SimpleLabelTooltipNodePainter extends DefaultNodePainter
{

	public String getLabel()
	{
		if(getNode() == null)
			return NOT_EDITABLE;
		
		String val = getTooltip();
		if("".equals(val) || NOT_EDITABLE.equals(val))
		  return "";
		return val.substring(val.lastIndexOf('.') + 1);
	}

	@Override
	public String getTooltip()
	{
		if(getNode() == null)
			return NOT_EDITABLE;
		
		Object o = getHG().get(getNode().getHandle());
		return o!= null ? o.toString() : "[null]";
	}
}

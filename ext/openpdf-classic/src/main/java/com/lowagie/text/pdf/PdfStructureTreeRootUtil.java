package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extension of {@link PdfStructureTreeRoot} that supports annotation
 * /StructParent entries in the ParentTree.
 *
 * For annotations with /StructParent (singular), the ParentTree value
 * must be a direct reference to the StructElem, not an array (which is
 * used for page /StructParents entries). This subclass overrides
 * {@link #buildTree()} to handle both types of entries correctly.
 *
 * Annotation /StructParent keys are assigned at build time to avoid
 * collisions with page /StructParents values (which are assigned by
 * OpenPDF using page indices 0, 1, 2, ...).
 */
public class PdfStructureTreeRootUtil extends PdfStructureTreeRoot
{

	private final List<AnnotationParent> annotationParents = new ArrayList<>();

	PdfStructureTreeRootUtil(PdfWriter writer)
	{
		super(writer);
	}

	/**
	 * Installs this extended tree root into the given PdfWriter,
	 * replacing the default PdfStructureTreeRoot.
	 * Must be called before any structure elements are created.
	 *
	 * @param writer the PdfWriter to install into
	 * @return the installed PdfStructureTreeRootUtil instance
	 */
	public static PdfStructureTreeRootUtil install(PdfWriter writer)
	{
		PdfStructureTreeRootUtil root = new PdfStructureTreeRootUtil(writer);
		writer.structureTreeRoot = root;
		return root;
	}

	/**
	 * Registers an annotation for /StructParent assignment.
	 * The actual ParentTree key is assigned later in {@link #buildTree()}
	 * to avoid collisions with page /StructParents values.
	 *
	 * @param annotation the annotation dictionary (its /StructParent will be set at build time)
	 * @param structElemReference the indirect reference to the StructElem
	 */
	public void addAnnotationParent(PdfAnnotation annotation, PdfIndirectReference structElemReference)
	{
		annotationParents.add(new AnnotationParent(annotation, structElemReference));
	}

	@Override
	void buildTree() throws IOException
	{
		PdfWriter writer = getWriter();

		Map<Integer, PdfArray> parentTree;
		try
		{
			java.lang.reflect.Field parentTreeField = PdfStructureTreeRoot.class.getDeclaredField("parentTree");
			parentTreeField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<Integer, PdfArray> pt = (Map<Integer, PdfArray>) parentTreeField.get(this);
			parentTree = pt;
		}
		catch (ReflectiveOperationException e)
		{
			throw new RuntimeException("Failed to access PdfStructureTreeRoot.parentTree", e);
		}

		Map<Integer, PdfIndirectReference> numTree = new HashMap<>();

		// Page-level entries: each value is a PdfArray of StructElem references
		int maxPageKey = -1;
		for (Map.Entry<Integer, PdfArray> entry : parentTree.entrySet())
		{
			numTree.put(entry.getKey(), writer.addToBody(entry.getValue()).getIndirectReference());
			if (entry.getKey() > maxPageKey)
			{
				maxPageKey = entry.getKey();
			}
		}

		// Annotation-level entries: assign keys starting after all page keys
		// to avoid collisions with page StructParents values (0, 1, 2, ...).
		// Annotations have already been written as part of their pages,
		// so we re-write them with the updated StructParent value.
		int nextKey = maxPageKey + 1;
		for (AnnotationParent ap : annotationParents)
		{
			ap.annotation.put(PdfName.STRUCTPARENT, new PdfNumber(nextKey));
			writer.addToBody(ap.annotation, ap.annotation.getIndirectReference());
			numTree.put(nextKey, ap.structElemReference);
			nextKey++;
		}

		PdfDictionary dicTree = PdfNumberTree.writeTree(numTree, writer);
		if (dicTree != null)
		{
			put(PdfName.PARENTTREE, writer.addToBody(dicTree).getIndirectReference());
		}

		nodeProcess(this, getReference(), writer);
	}

	private void nodeProcess(PdfDictionary dictionary, PdfIndirectReference reference, PdfWriter writer)
			throws IOException
	{
		PdfObject obj = dictionary.get(PdfName.K);
		if (obj != null && obj.isArray() && !((PdfArray) obj).getElements().isEmpty()
				&& !((PdfArray) obj).getElements().get(0).isNumber())
		{
			PdfArray ar = (PdfArray) obj;
			for (int k = 0; k < ar.size(); ++k)
			{
				PdfObject element = ar.getDirectObject(k);
				if (element instanceof PdfStructureElement)
				{
					PdfStructureElement e = (PdfStructureElement) element;
					ar.set(k, e.getReference());
					nodeProcess(e, e.getReference(), writer);
				}
			}
		}
		if (reference != null)
		{
			writer.addToBody(dictionary, reference);
		}
	}

	private static class AnnotationParent
	{
		final PdfAnnotation annotation;
		final PdfIndirectReference structElemReference;

		AnnotationParent(PdfAnnotation annotation, PdfIndirectReference structElemReference)
		{
			this.annotation = annotation;
			this.structElemReference = structElemReference;
		}
	}

}

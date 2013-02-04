package com.googlecode.httpfilter.proxy.rabbit.jndi;

/*
 * "@(#)HierCtx.java	1.1	00/01/18 SMI"
 *
 * Copyright 1997, 1998, 1999 Sun Microsystems, Inc. All Rights
 * Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free,
 * license to use, modify and redistribute this software in source and
 * binary code form, provided that i) this copyright notice and license
 * appear on all copies of the software; and ii) Licensee does not
 * utilize the software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED.  SUN AND ITS LICENSORS SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN
 * NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT
 * OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line
 * control of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and warrants
 * that it will not use or redistribute the Software for such purposes.
 */

import javax.naming.*;
import javax.naming.spi.*;
import java.util.*;

/**
 * A sample service provider that implements a hierarchical namespace in memory.
 * 
 * Originally from the jndi tutorial, but patched quite a bit
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HierContext implements Context {
	protected Hashtable<Object, Object> myEnv = new Hashtable<Object, Object>();
	protected Map<String, Object> bindings = new HashMap<String, Object>();
	protected final static NameParser myParser = new HierParser();
	protected HierContext parent = null;
	protected String myAtomicName = null;

	HierContext(Hashtable<?, ?> env) {
		if (env != null) {
			for (Map.Entry<?, ?> me : env.entrySet())
				myEnv.put(me.getKey(), me.getValue());
		}
	}

	protected HierContext(HierContext parent, String name,
			Hashtable<?, ?> inEnv, Map<String, Object> bindings) {
		this(inEnv);
		this.parent = parent;
		myAtomicName = name;
		this.bindings = new HashMap<String, Object>(bindings);
	}

	protected Context createCtx(HierContext parent, String name,
			Hashtable<?, ?> inEnv) {
		return new HierContext(parent, name, inEnv,
				new HashMap<String, Object>());
	}

	protected Context cloneCtx() {
		return new HierContext(parent, myAtomicName, myEnv, bindings);
	}

	public Object lookup(String name) throws NamingException {
		return lookup(new CompositeName(name));
	}

	public Object lookup(Name name) throws NamingException {
		if (name.isEmpty()) {
			// Asking to look up this context itself. Create and return
			// a new instance with its own independent environment.
			return cloneCtx();
		}

		// Extract components that belong to this namespace
		String atom = name.get(0);
		Object inter = bindings.get(atom);
		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			if (inter == null) {
				throw new NameNotFoundException(name + " not found");
			}

			// Call getObjectInstance for using any object factories
			try {
				Name cn = new CompositeName().add(atom);
				return NamingManager.getObjectInstance(inter, cn, this, myEnv);
			} catch (Exception e) {
				NamingException ne = new NamingException(
						"getObjectInstance failed");
				ne.setRootCause(e);
				throw ne;
			}
		}
		// Intermediate name: Consume name in this context and continue
		if (!(inter instanceof Context)) {
			String err = atom + " does not name a context, " + "bindings: "
					+ bindings;
			throw new NotContextException(err);
		}

		return ((Context) inter).lookup(name.getSuffix(1));
	}

	public void bind(String name, Object obj) throws NamingException {
		bind(new CompositeName(name), obj);
	}

	public void bind(Name name, Object obj) throws NamingException {
		if (name.isEmpty()) {
			throw new InvalidNameException("Cannot bind empty name");
		}

		// Extract components that belong to this namespace
		String atom = name.get(0);
		Object inter = bindings.get(atom);

		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			if (inter != null) {
				throw new NameAlreadyBoundException("Use rebind to override");
			}

			// Call getStateToBind for using any state factories
			Name cn = new CompositeName().add(atom);
			obj = NamingManager.getStateToBind(obj, cn, this, myEnv);

			// Add object to internal data structure
			bindings.put(atom, obj);
		} else {
			// Intermediate name: Consume name in this context and continue
			if (inter == null) {
				Context c = createSubcontext(atom);
				c.bind(name.getSuffix(1), obj);
			} else if (inter instanceof Context) {
				((Context) inter).bind(name.getSuffix(1), obj);
			} else {
				String err = "Intermediate already bound and "
						+ "it is not a context: name: " + name + ", inter: "
						+ inter;
				throw new NotContextException(err);
			}
		}
	}

	public void rebind(String name, Object obj) throws NamingException {
		rebind(new CompositeName(name), obj);
	}

	public void rebind(Name name, Object obj) throws NamingException {
		if (name.isEmpty()) {
			throw new InvalidNameException("Cannot bind empty name");
		}

		// Extract components that belong to this namespace
		String atom = name.get(0);

		if (name.size() == 1) {
			// Atomic name

			// Call getStateToBind for using any state factories
			Name cn = new CompositeName().add(atom);
			obj = NamingManager.getStateToBind(obj, cn, this, myEnv);

			// Add object to internal data structure
			bindings.put(atom, obj);
		} else {
			// Intermediate name: Consume name in this context and continue
			Object inter = bindings.get(atom);
			if (!(inter instanceof Context)) {
				throw new NotContextException(atom + " does not name a context");
			}
			((Context) inter).rebind(name.getSuffix(1), obj);
		}
	}

	public void unbind(String name) throws NamingException {
		unbind(new CompositeName(name));
	}

	public void unbind(Name name) throws NamingException {
		if (name.isEmpty()) {
			throw new InvalidNameException("Cannot unbind empty name");
		}

		// Extract components that belong to this namespace
		String atom = name.get(0);

		// Remove object from internal data structure
		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			bindings.remove(atom);
		} else {
			// Intermediate name: Consume name in this context and continue
			Object inter = bindings.get(atom);
			if (!(inter instanceof Context)) {
				throw new NotContextException(atom + " does not name a context");
			}
			((Context) inter).unbind(name.getSuffix(1));
		}
	}

	public void rename(String oldname, String newname) throws NamingException {
		rename(new CompositeName(oldname), new CompositeName(newname));
	}

	public void rename(Name oldname, Name newname) throws NamingException {
		if (oldname.isEmpty() || newname.isEmpty()) {
			throw new InvalidNameException("Cannot rename empty name");
		}

		// Simplistic implementation: support only rename within same context
		if (oldname.size() != newname.size()) {
			String err = "Do not support rename across different contexts";
			throw new OperationNotSupportedException(err);
		}

		String oldatom = oldname.get(0);
		String newatom = newname.get(0);

		if (oldname.size() == 1) {
			// Atomic name: Add object to internal data structure
			// Check if new name exists
			if (bindings.get(newatom) != null) {
				throw new NameAlreadyBoundException(newname.toString()
						+ " is already bound");
			}

			// Check if old name is bound
			Object oldBinding = bindings.remove(oldatom);
			if (oldBinding == null) {
				throw new NameNotFoundException(oldname.toString()
						+ " not bound");
			}

			bindings.put(newatom, oldBinding);
		} else {
			// Simplistic implementation: support only rename within same
			// context
			if (!oldatom.equals(newatom)) {
				String err = "Do not support rename across different contexts";
				throw new OperationNotSupportedException(err);
			}

			// Intermediate name: Consume name in this context and continue
			Object inter = bindings.get(oldatom);
			if (!(inter instanceof Context)) {
				throw new NotContextException(oldatom
						+ " does not name a context");
			}
			((Context) inter)
					.rename(oldname.getSuffix(1), newname.getSuffix(1));
		}
	}

	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		return list(new CompositeName(name));
	}

	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		if (name.isEmpty()) {
			// listing this context
			return new ListOfNames(bindings.keySet());
		}

		// Perhaps 'name' names a context
		Object target = lookup(name);
		if (target instanceof Context) {
			return ((Context) target).list("");
		}
		throw new NotContextException(name + " cannot be listed");
	}

	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		return listBindings(new CompositeName(name));
	}

	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		if (name.isEmpty()) {
			// listing this context
			return new ListOfBindings(bindings.keySet());
		}

		// Perhaps 'name' names a context
		Object target = lookup(name);
		if (target instanceof Context) {
			return ((Context) target).listBindings("");
		}
		throw new NotContextException(name + " cannot be listed");
	}

	public void destroySubcontext(String name) throws NamingException {
		destroySubcontext(new CompositeName(name));
	}

	public void destroySubcontext(Name name) throws NamingException {
		if (name.isEmpty()) {
			throw new InvalidNameException(
					"Cannot destroy context using empty name");
		}

		// Simplistic implementation: not checking for nonempty context first
		// Use same implementation as unbind
		unbind(name);
	}

	public Context createSubcontext(String name) throws NamingException {
		return createSubcontext(new CompositeName(name));
	}

	public Context createSubcontext(Name name) throws NamingException {
		if (name.isEmpty()) {
			throw new InvalidNameException("Cannot bind empty name");
		}

		// Extract components that belong to this namespace
		String atom = name.get(0);
		Object inter = bindings.get(atom);

		if (name.size() == 1) {
			// Atomic name: Find object in internal data structure
			if (inter != null) {
				throw new NameAlreadyBoundException("Use rebind to override");
			}

			// Create child
			Context child = createCtx(this, atom, myEnv);

			// Add child to internal data structure
			bindings.put(atom, child);
			return child;
		}
		// Intermediate name: Consume name in this context and continue
		if (!(inter instanceof Context)) {
			String err = atom + " does not name a context" + ", bindings: "
					+ bindings;
			throw new NotContextException(err);
		}
		return ((Context) inter).createSubcontext(name.getSuffix(1));
	}

	public Object lookupLink(String name) throws NamingException {
		return lookupLink(new CompositeName(name));
	}

	public Object lookupLink(Name name) throws NamingException {
		return lookup(name);
	}

	public NameParser getNameParser(String name) throws NamingException {
		return getNameParser(new CompositeName(name));
	}

	public NameParser getNameParser(Name name) throws NamingException {
		// Do lookup to verify name exists
		Object obj = lookup(name);
		if (obj instanceof Context) {
			((Context) obj).close();
		}
		return myParser;
	}

	public String composeName(String name, String prefix)
			throws NamingException {
		Name result = composeName(new CompositeName(name), new CompositeName(
				prefix));
		return result.toString();
	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		Name result;

		// Both are compound names, compose using compound name rules
		if (!(name instanceof CompositeName)
				&& !(prefix instanceof CompositeName)) {
			result = (Name) (prefix.clone());
			result.addAll(name);
			return new CompositeName().add(result.toString());
		}

		// Simplistic implementation: do not support federation
		String err = "Do not support composing composite names";
		throw new OperationNotSupportedException(err);
	}

	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		return myEnv.put(propName, propVal);
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		return myEnv.remove(propName);
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return new Hashtable<Object, Object>(myEnv);
	}

	public String getNameInNamespace() throws NamingException {
		HierContext ancestor = parent;

		// No ancestor
		if (ancestor == null) {
			return "";
		}

		Name name = myParser.parse("");
		name.add(myAtomicName);

		// Get parent's names
		while (ancestor != null && ancestor.myAtomicName != null) {
			name.add(0, ancestor.myAtomicName);
			ancestor = ancestor.parent;
		}

		return name.toString();
	}

	@Override
	public String toString() {
		if (myAtomicName != null)
			return myAtomicName;
		return "ROOT CONTEXT";
	}

	public void close() throws NamingException {
		// empty
	}

	private abstract class ListOf<T extends NameClassPair> implements
			NamingEnumeration<T> {
		protected Iterator<String> names;

		ListOf(Collection<String> names) {
			this.names = names.iterator();
		}

		public boolean hasMoreElements() {
			try {
				return hasMore();
			} catch (NamingException e) {
				return false;
			}
		}

		public boolean hasMore() throws NamingException {
			return names.hasNext();
		}

		public abstract T next() throws NamingException;

		public T nextElement() {
			try {
				return next();
			} catch (NamingException e) {
				throw new NoSuchElementException(e.toString());
			}
		}

		public void close() {
			// empty
		}
	}

	// Class for enumerating name/class pairs
	private class ListOfNames extends ListOf<NameClassPair> {
		ListOfNames(Collection<String> names) {
			super(names);
		}

		@Override
		public NameClassPair next() throws NamingException {
			String name = names.next();
			String className = bindings.get(name).getClass().getName();
			return new NameClassPair(name, className);
		}
	}

	// Class for enumerating bindings
	private class ListOfBindings extends ListOf<Binding> {

		ListOfBindings(Collection<String> names) {
			super(names);
		}

		@Override
		public Binding next() throws NamingException {
			String name = names.next();
			Object obj = bindings.get(name);

			try {
				obj = NamingManager.getObjectInstance(obj,
						new CompositeName().add(name), HierContext.this,
						HierContext.this.myEnv);
			} catch (Exception e) {
				NamingException ne = new NamingException(
						"getObjectInstance failed");
				ne.setRootCause(e);
				throw ne;
			}
			return new Binding(name, obj);
		}
	}
}

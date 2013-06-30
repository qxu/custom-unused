package custom;

import java.util.ArrayList;
import java.util.List;

import com.github.antlrjavaparser.api.Comment;
import com.github.antlrjavaparser.api.ImportDeclaration;
import com.github.antlrjavaparser.api.PackageDeclaration;
import com.github.antlrjavaparser.api.TypeParameter;
import com.github.antlrjavaparser.api.body.CatchParameter;
import com.github.antlrjavaparser.api.body.ClassOrInterfaceDeclaration;
import com.github.antlrjavaparser.api.body.ConstructorDeclaration;
import com.github.antlrjavaparser.api.body.EmptyTypeDeclaration;
import com.github.antlrjavaparser.api.body.EnumConstantDeclaration;
import com.github.antlrjavaparser.api.body.EnumDeclaration;
import com.github.antlrjavaparser.api.body.FieldDeclaration;
import com.github.antlrjavaparser.api.body.MethodDeclaration;
import com.github.antlrjavaparser.api.body.Parameter;
import com.github.antlrjavaparser.api.body.Resource;
import com.github.antlrjavaparser.api.expr.AnnotationExpr;
import com.github.antlrjavaparser.api.expr.Expression;
import com.github.antlrjavaparser.api.expr.FieldAccessExpr;
import com.github.antlrjavaparser.api.expr.MemberValuePair;
import com.github.antlrjavaparser.api.expr.MethodCallExpr;
import com.github.antlrjavaparser.api.expr.NameExpr;
import com.github.antlrjavaparser.api.expr.NormalAnnotationExpr;
import com.github.antlrjavaparser.api.expr.NullLiteralExpr;
import com.github.antlrjavaparser.api.expr.ObjectCreationExpr;
import com.github.antlrjavaparser.api.expr.VariableDeclarationExpr;
import com.github.antlrjavaparser.api.stmt.ExplicitConstructorInvocationStmt;
import com.github.antlrjavaparser.api.stmt.ExpressionStmt;
import com.github.antlrjavaparser.api.type.ClassOrInterfaceType;
import com.github.antlrjavaparser.api.type.ReferenceType;
import com.github.antlrjavaparser.api.type.Type;
import com.github.antlrjavaparser.api.visitor.VoidVisitorAdapter;

import custom.factory.ClassFactory;
import custom.factory.ClassFactoryResolver;
import custom.factory.MethodFactory;
import custom.factory.ResolvingClassAdapter;

public class CustomClassVisitor extends VoidVisitorAdapter<Object>
{
	private CustomSourceReader source;
	ClassFactoryResolver classResolver;

	List<ClassFactory> classes;

	private String currentPackageName;
	private ClassFactory currentClass;
	private MethodFactory currentMethod;

	List<ResolvingClassAdapter> resolveList = new ArrayList<>();

	public CustomClassVisitor(CustomSourceReader source)
	{
		this.source = source;
		this.classResolver = new ClassFactoryResolver();
		this.classes = new ArrayList<>();
	}

	public void visit(PackageDeclaration n, Object arg)
	{
		String pkgName = n.getName().getName();
		currentPackageName = pkgName;
		for(String className : source.getClassNamesInPackage(pkgName))
		{
			classResolver.addPackage(className);
		}
		super.visit(n, arg);
	}

	public void visit(ImportDeclaration n, Object arg)
	{
		String className = n.getName().getName();
		classResolver.addImport(className);
		super.visit(n, arg);
	}

	public void visit(ClassOrInterfaceDeclaration n, Object arg)
	{
		ClassFactory parent = currentClass;
		currentClass = (parent == null)
				? ClassFactory.create(currentPackageName + "." + n.getName())
				: ClassFactory.create(parent.getName() + "$" + n.getName());
		classes.add(currentClass);
		classResolver.addLocalClass(currentClass);

		super.visit(n, arg);

		currentClass = parent;
	}

	public void visit(ConstructorDeclaration n, Object arg)
	{
		MethodFactory scopeMethod = currentMethod;
		currentMethod = new MethodFactory("", currentClass);
		currentClass.addMethod(currentMethod);

		super.visit(n, arg);

		currentMethod = scopeMethod;
	}

	public void visit(MethodDeclaration n, Object arg)
	{
		MethodFactory scopeMethod = currentMethod;
		currentMethod = new MethodFactory(n.getName(), currentClass);
		currentClass.addMethod(currentMethod);

		super.visit(n, arg);

		currentMethod = scopeMethod;
	}

	public void visit(Parameter n, Object arg)
	{
		if(n.getAnnotations() != null)
		{
			for(AnnotationExpr a : n.getAnnotations())
			{
				a.accept(this, arg);
			}
		}
		Type t = n.getType();
		while(t instanceof ReferenceType)
		{
			t = ((ReferenceType)t).getType();
		}
		if(t instanceof ClassOrInterfaceType)
		{
			ClassOrInterfaceType coit = (ClassOrInterfaceType)t;
			StringBuilder scopeSb = new StringBuilder();
			ClassOrInterfaceType scope = coit.getScope();
			while(scope != null)
			{
				scopeSb.append(scope.getName());
				scopeSb.append(".");
				scope = scope.getScope();
			}
			String declaredName = scopeSb.toString() + coit.getName();
			currentMethod.addParameter(new ResolvingClassAdapter(declaredName, classResolver));
		}
		
		System.out.println("[T]" + n.getType().getClass());
	}

	public void visit(ClassOrInterfaceType n, Object arg)
	{
		StringBuilder scopeSb = new StringBuilder();
		ClassOrInterfaceType scope = n.getScope();
		while(scope != null)
		{
			scopeSb.append(scope.getName());
			scopeSb.append(".");
			scope = scope.getScope();
		}
		String declaredName = scopeSb.toString() + n.getName();

		if(currentMethod != null)
		{
			currentMethod.addVariable(new ResolvingClassAdapter(declaredName, classResolver));
		}
		else if(currentClass != null)
		{
			currentClass.addField(new ResolvingClassAdapter(declaredName, classResolver));
		}
		if(n.getTypeArgs() != null)
		{
			for(Type t : n.getTypeArgs())
			{
				t.accept(this, arg);
			}
		}
	}

	public void visit(EnumConstantDeclaration n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(EnumDeclaration n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(ExplicitConstructorInvocationStmt n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(ExpressionStmt n, Object arg)
	{
		// n.getExpression().getClass().getSimpleName();
		// System.out.println("[ES] " + n + " : " +
		// n.getExpression().getClass().getSimpleName());
		super.visit(n, arg);
	}

	public void visit(FieldAccessExpr n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(FieldDeclaration n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(MemberValuePair n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(MethodCallExpr n, Object arg)
	{
		Expression scope = n.getScope();
		// System.out.println("[SC] " + (scope != null ?
		// scope.getClass().getSimpleName() : null));
		if(scope instanceof NameExpr)
		{
			NameExpr expr = (NameExpr)scope;

			// System.out.println("method call: " + n);
		}
		else if(scope instanceof FieldAccessExpr)
		{
			FieldAccessExpr expr = (FieldAccessExpr)scope;

			// System.out.println("[FA] " + expr.getScope());
		}
		super.visit(n, arg);
	}

	public void visit(NameExpr n, Object arg)
	{
	}

	public void visit(NormalAnnotationExpr n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(NullLiteralExpr n, Object arg)
	{
	}

	public void visit(ObjectCreationExpr n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(ReferenceType n, Object arg)
	{
		super.visit(n, arg);
	}

	public void visit(VariableDeclarationExpr n, Object arg)
	{
		super.visit(n, arg);
	}

	@Override
	public void visit(Comment n, Object arg)
	{
	}

	@Override
	public void visit(CatchParameter n, Object arg)
	{
	}

	@Override
	public void visit(Resource n, Object arg)
	{
	}
}
